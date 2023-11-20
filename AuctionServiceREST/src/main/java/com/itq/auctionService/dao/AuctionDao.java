package com.itq.auctionService.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import com.itq.auctionService.dto.Auction;
import com.itq.auctionService.dto.Bid;
import com.itq.auctionService.service.CustomAuctionException;

@Repository
public class AuctionDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public class AuctionRowMapper implements RowMapper<Auction>{
        @Override
        public Auction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Auction auction = new Auction();
            auction.setAuctionId(rs.getInt("auctionId"));
            auction.setInitialPrice(rs.getDouble("initialPrice"));
            auction.setFinalPrice(rs.getDouble("finalPrice"));
            auction.setAuctionDate(rs.getString("date"));
            auction.setAuctionStatus(rs.getString("status"));
            auction.setProviderId(rs.getInt("providerId"));
            auction.setProductId(rs.getInt("productId"));
            return auction;
        }
    }

    public class BidRowMapper implements RowMapper<Bid>{
        @Override
        public Bid mapRow(ResultSet rs, int rowNum) throws SQLException {
            Bid bid = new Bid();
            bid.setBidId(rs.getInt("auctionBidsId"));
            bid.setDate(rs.getString("date"));
            bid.setPrice(rs.getDouble("price"));
            bid.setClientId(rs.getInt("clientId"));
            bid.setAuctionId(rs.getInt("auctionId"));
            bid.setProviderId(rs.getInt("providerId"));
            bid.setProductId(rs.getInt("productId"));
            return bid;
        }
    }

    @SuppressWarnings("deprecation")
	public boolean createAuction(final Auction auction) {

        // check if the product exists
        if (!jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM products WHERE productId = ?)", new Object[]{auction.getProductId()}, Boolean.class)) {
            String errorMessage = "Product with id: {" + auction.getProductId() + "} does not exist on the database.";
            LOGGER.error(errorMessage);
            throw new CustomAuctionException(errorMessage);
        }

        // check if the provider exists and if is a provider
        if (!jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM users WHERE userId = ? AND type = 'Provider')", new Object[]{auction.getProviderId()}, Boolean.class)) {
            String errorMessage = "User with id: {" + auction.getProviderId() + "} does not exist or is not a provider.";
            LOGGER.error(errorMessage);
            throw new CustomAuctionException(errorMessage);
        }

        StringBuffer auctionSql = new StringBuffer("");
        auctionSql.append("INSERT INTO auctions (auctionId, initialPrice, finalPrice, date, status, providerId, productId) ");
        auctionSql.append("VALUES (?, ?, ?, ?, ?, ?, ?)");

        final String auctionQuery = auctionSql.toString();

        try {
            GeneratedKeyHolder auctionKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement auctionPreparedStatement = connection.prepareStatement(auctionQuery, Statement.RETURN_GENERATED_KEYS);
                    auctionPreparedStatement.setInt(1, auction.getAuctionId());
                    auctionPreparedStatement.setDouble(2, auction.getInitialPrice());
                    auctionPreparedStatement.setDouble(3, auction.getInitialPrice());
                    auctionPreparedStatement.setString(4, auction.getAuctionDate());
                    auctionPreparedStatement.setString(5, auction.getAuctionStatus());
                    auctionPreparedStatement.setInt(6, auction.getProviderId());
                    auctionPreparedStatement.setInt(7, auction.getProductId());
                    return auctionPreparedStatement;
                }
            }, auctionKeyHolder);
            LOGGER.info("Auction created with id: {" + auctionKeyHolder.getKey().intValue() + "}");

            //Once the auction is created, we need to update the stock of the product
            jdbcTemplate.update("UPDATE products SET stock = stock - 1 WHERE productId = ?", auction.getProductId());
            LOGGER.info("Stock of product with id: {" + auction.getProductId() + "} updated.");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error creating auction on the database. Message: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("deprecation")
	public boolean updateAuction(int auctionId, Auction auction) {
        
        // check if the auction exists
        if (!jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM auctions WHERE auctionId = ?)", new Object[]{auctionId}, Boolean.class)) {
            String errorMessage = "Auction with id: {" + auctionId + "} does not exist on the database.";
            LOGGER.error(errorMessage);
            throw new CustomAuctionException(errorMessage);
        }

        StringBuffer auctionSql = new StringBuffer("");
        auctionSql.append("UPDATE auctions SET initialPrice = ?, finalPrice = ?, date = ?, status = ?, providerId = ?, productId = ? ");
        auctionSql.append("WHERE auctionId = ?");

        final String auctionQuery = auctionSql.toString();

        try {
            jdbcTemplate.update(auctionQuery, auction.getInitialPrice(), auction.getFinalPrice(), auction.getAuctionDate(), auction.getAuctionStatus(), auction.getProviderId(), auction.getProductId(), auctionId);
            LOGGER.info("Auction with id: {" + auctionId + "} updated.");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error updating auction on the database. Message: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("deprecation")
	public Auction getAuctionById(int auctionId) {
        StringBuffer auctionSql = new StringBuffer("");
        auctionSql.append("SELECT auctionId, initialPrice, finalPrice, date, status, providerId, productId ");
        auctionSql.append("FROM auctions ");
        auctionSql.append("WHERE auctionId = ?");

        final String auctionQuery = auctionSql.toString();

        try {
            LOGGER.info("Auction with id: {" + auctionId + "} retrieved succesfully from the database.");
            return jdbcTemplate.queryForObject(auctionQuery, new Object[]{auctionId}, new AuctionRowMapper());
        } catch (Exception e) {
            LOGGER.error("Error getting auction from the database. Message: " + e.getMessage());
            return null;
        }
    }

    public List<Auction> getAllAuctions() {
        StringBuffer auctionSql = new StringBuffer("");
        auctionSql.append("SELECT * FROM auctions");

        final String auctionQuery = auctionSql.toString();

        try {
            LOGGER.info("All the auctions retrieved succesfully from the database.");
            return jdbcTemplate.query(auctionQuery, new AuctionRowMapper());
        } catch (Exception e) {
            LOGGER.error("Error getting all the auctions from the database. Message: " + e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("deprecation")
	public List<Auction> getAuctionsByProviderId(int providerId) {
        StringBuffer auctionSql = new StringBuffer("");
        auctionSql.append("SELECT * FROM auctions ");
        auctionSql.append("WHERE providerId = ?");

        final String auctionQuery = auctionSql.toString();

        try {
            LOGGER.info("Auctions retrieved succesfully from the database for provider with id: {" + providerId + "}.");
            return jdbcTemplate.query(auctionQuery, new Object[]{providerId}, new AuctionRowMapper());
        } catch (Exception e) {
            LOGGER.error("Error getting auctions from the database for provider with id: {" + providerId + "}. Message: " + e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public List<Auction> getAuctionsByUserId(int userId) {
        StringBuffer auctionSql = new StringBuffer("");
        auctionSql.append("SELECT * FROM auctions ");
        auctionSql.append("WHERE clientId = ?");

        final String auctionQuery = auctionSql.toString();

        try {
            LOGGER.info("Auctions retrieved succesfully from the database for user with id: {" + userId + "}.");
            return jdbcTemplate.query(auctionQuery, new Object[]{userId}, new AuctionRowMapper());
        } catch (Exception e) {
            LOGGER.error("Error getting auctions from the database for user with id: {" + userId + "}. Message: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteAuction(int auctionId) {
        StringBuffer auctionSql = new StringBuffer("");
        auctionSql.append("DELETE FROM auctions ");
        auctionSql.append("WHERE auctionId = ?");

        final String auctionQuery = auctionSql.toString();

        try {
            jdbcTemplate.update(auctionQuery, auctionId);
            // delete all the bids related to the auction
            jdbcTemplate.update("DELETE FROM pujas WHERE auctionId = ?", auctionId);
            LOGGER.info("Auction with id: {" + auctionId + "} deleted succesfully from the database.");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error deleting auction with id: {" + auctionId + "} from the database. Message: " + e.getMessage());
            return false;
        }
    }

    //Methods related to the bids of an auction

    @SuppressWarnings("deprecation")
	public boolean createBid(int auctionId, Bid bid) {

        // check if the auction exists
        if (!jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM auctions WHERE auctionId = ?)", new Object[]{auctionId}, Boolean.class)) {
            String errorMessage = "Auction with id: {" + auctionId + "} does not exist on the database.";
            LOGGER.error(errorMessage);
            throw new CustomAuctionException(errorMessage);
        }

        // check if the user exists and if is a client
        int userId = bid.getClientId();
        if (!jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM users WHERE userId = ? AND type = 'Client')", new Object[]{userId}, Boolean.class)) {
            String errorMessage = "User with id: {" + userId + "} does not exist or is not a client.";
            LOGGER.error(errorMessage);
            throw new CustomAuctionException(errorMessage);
        }

        // retrieve the values of the provider and the product from the auction
        int providerId = jdbcTemplate.queryForObject("SELECT providerId FROM auctions WHERE auctionId = ?", new Object[]{auctionId}, Integer.class);
        int productId = jdbcTemplate.queryForObject("SELECT productId FROM auctions WHERE auctionId = ?", new Object[]{auctionId}, Integer.class);

        // check if the amount of the bid is higher than the initial price of the auction
        if (bid.getPrice() < jdbcTemplate.queryForObject("SELECT initialPrice FROM auctions WHERE auctionId = ?", new Object[]{auctionId}, Double.class)) {
            String errorMessage = "The amount of the bid is lower than the initial price of the auction.";
            LOGGER.error(errorMessage);
            throw new CustomAuctionException(errorMessage);
        }

        // check if the amount of the bid is higher than the final price of the auction
        if (bid.getPrice() < jdbcTemplate.queryForObject("SELECT finalPrice FROM auctions WHERE auctionId = ?", new Object[]{auctionId}, Double.class)) {
            String errorMessage = "The amount of the bid is lower than the actual price of the auction.";
            LOGGER.error(errorMessage);
            throw new CustomAuctionException(errorMessage);
        }
        
        StringBuffer bidSql = new StringBuffer("");
        bidSql.append("INSERT INTO pujas (auctionBidsId, date, price, clientId, auctionId, providerId, productId) ");
        bidSql.append("VALUES (?, ?, ?, ?, ?, ?, ?)");

        final String bidQuery = bidSql.toString();

        try {
            GeneratedKeyHolder bidKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement bidPreparedStatement = connection.prepareStatement(bidQuery, Statement.RETURN_GENERATED_KEYS);
                    bidPreparedStatement.setInt(1, bid.getBidId());
                    bidPreparedStatement.setString(2, bid.getDate());
                    bidPreparedStatement.setDouble(3, bid.getPrice());
                    bidPreparedStatement.setInt(4, userId);
                    bidPreparedStatement.setInt(5, auctionId);
                    bidPreparedStatement.setInt(6, providerId);
                    bidPreparedStatement.setInt(7, productId);
                    return bidPreparedStatement;
                }
            }, bidKeyHolder);
            LOGGER.info("Bid created with id: {" + bidKeyHolder.getKey().intValue() + "}");

            //Once the bid is created, we need to update the final price of the auction
            jdbcTemplate.update("UPDATE auctions SET finalPrice = ? WHERE auctionId = ?", bid.getPrice(), auctionId);
            LOGGER.info("Final price of auction with id: {" + auctionId + "} updated.");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error creating bid on the database. Message: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("deprecation")
	public List<Bid> getBidsByAuctionId(int auctionId) {
        StringBuffer bidSql = new StringBuffer("");
        bidSql.append("SELECT * FROM pujas ");
        bidSql.append("WHERE auctionId = ?");

        final String bidQuery = bidSql.toString();

        try {
            LOGGER.info("Bids retrieved succesfully from the database for auction with id: {" + auctionId + "}.");
            return jdbcTemplate.query(bidQuery, new Object[]{auctionId}, new BidRowMapper());
        } catch (Exception e) {
            LOGGER.error("Error getting bids from the database for auction with id: {" + auctionId + "}. Message: " + e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public Bid getHighestBidByAuctionId(int auctionId) {
        StringBuffer bidSql = new StringBuffer("");
        bidSql.append("SELECT * FROM pujas ");
        bidSql.append("WHERE auctionId = ? ");
        bidSql.append("ORDER BY price DESC ");
        bidSql.append("LIMIT 1");

        final String bidQuery = bidSql.toString();

        try {
            LOGGER.info("Highest bid retrieved succesfully from the database for auction with id: {" + auctionId + "}.");
            return jdbcTemplate.queryForObject(bidQuery, new Object[]{auctionId}, new BidRowMapper());
        } catch (Exception e) {
            LOGGER.error("Error getting highest bid from the database for auction with id: {" + auctionId + "}. Message: " + e.getMessage());
            return null;
        }
    }
}
