package com.itq.saleService.dao;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.itq.saleService.dto.Sale;
import com.itq.saleService.service.CustomSaleException;

@Repository
public class SaleDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaleDao.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    public boolean existSale(int saleId) {
        String sql = "SELECT COUNT(*) FROM sales WHERE saleId = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, saleId);
        return count > 0;
    }
    public boolean existUser(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE userId = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count > 0;
    }
    public boolean existClient(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE type = 'Client' AND userId = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count > 0;
    }
    public boolean existProduct(int productId) {
        String sql = "SELECT COUNT(*) FROM products WHERE productId = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, productId);
        return count > 0;
    }
    public boolean existDate(String date) {
        String sql = "SELECT COUNT(*) FROM sales WHERE date = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, date);
        return count > 0;
    }
    public boolean existCategory(String category) {
        String sql = "SELECT COUNT(*) FROM sales WHERE category = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, category);
        return count > 0;
    }
    public boolean verifyCategory(String category) {
        String[] allowedCategories = {"Food", "Clothes", "Electronics", "Home", "Health", "Beauty", "Automotive", "Shoes", "Other"};
        for (String allowedCategory : allowedCategories) {
            if (category.equalsIgnoreCase(allowedCategory)) {
                return true;
            }
        }
        return false;
    }
    public String getProductCategory(int productId) {
    	String sql = "SELECT category FROM products WHERE productId = ?";
    	return jdbcTemplate.queryForObject(sql, String.class, productId);
    }
    public int getProductProvider(int productId) {
    	String sql = "SELECT providerId FROM products WHERE productId = ?";
    	return jdbcTemplate.queryForObject(sql, Integer.class, productId);
     
    }
    public float getProductPrice(int productId) {
    	String sql = "SELECT productPrice FROM products WHERE productId = ?";
    	return jdbcTemplate.queryForObject(sql, Float.class, productId);
    }
    public String getProductName(int productId) {
    	String sql = "SELECT productName FROM products WHERE productId = ?";
    	return jdbcTemplate.queryForObject(sql, String.class, productId);
    }
    
    public class SaleRowMapper implements RowMapper<Sale> {
    	@Override
        public Sale mapRow(ResultSet rs, int rowNum) throws SQLException {
            Sale sale = new Sale();
            sale.setSaleId(rs.getInt("saleId"));
            sale.setTotal(rs.getFloat("total"));
            sale.setDate(rs.getString("date"));
            sale.setProviderId(rs.getInt("providerId"));
            sale.setProductId(rs.getInt("productId"));
            sale.setClientId(rs.getInt("clientId"));
            sale.setSaleName(rs.getString("saleName"));
            sale.setProductPrice(rs.getFloat("productPrice"));
            sale.setProductQuantity(rs.getInt("productQuantity"));
            sale.setStatus(rs.getString("status"));
            sale.setSaleCategory(rs.getString("category"));
            return sale;
        }
    }

	public boolean createSale(final Sale sale) {
		int clientId = sale.getClientId();
		int productId = sale.getProductId();
		
		//Verify that the client exist in the bd
		if(!this.existUser(clientId)){
			String errorMessage ="Error 404. Client with ID {"+ clientId+"} either does not exist on the database.";
			LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
			
		}

		//Verify that the product exist in the bd
		if(!this.existProduct(productId)){
			String errorMessage="Error 404: Product with ID {"+productId+"} either does not exist on the database.";
			LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
			
		}
		
		int providerId = this.getProductProvider(productId);
		float productPrice = this.getProductPrice(productId);
		String productCategory = this.getProductCategory(productId);
		float total = sale.getProductQuantity() * productPrice;
		String saleName = "Sale of "+ this.getProductName(productId);
		
		//Verify the category of product and sale 
		StringBuffer saleSql = new StringBuffer("");
        saleSql.append("INSERT INTO sales (saleId, productId, providerId, clientId, saleName, productPrice, date, productQuantity, total, status, category) ");
        saleSql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?)");
        final String saleQuery = saleSql.toString();
        
        try {
	        GeneratedKeyHolder saleKeyHolder = new GeneratedKeyHolder();
	        jdbcTemplate.update(new PreparedStatementCreator() {
            	@Override
	            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	                PreparedStatement ps = connection.prepareStatement(saleQuery, java.sql.Statement.RETURN_GENERATED_KEYS);
	                ps.setInt(1, sale.getSaleId());
	                ps.setInt(2, sale.getProductId());
	                ps.setInt(3, providerId);
	                ps.setInt(4, sale.getClientId());
	                ps.setString(5, saleName);
	                ps.setFloat(6, productPrice);
	                ps.setString(7, sale.getDate());
	                ps.setInt(8, sale.getProductQuantity());
	                ps.setFloat(9, total);
	                ps.setString(10, sale.getStatus());
	                ps.setString(11, productCategory);
	                return ps;
	            }
	        }, saleKeyHolder);
	        LOGGER.info("Sale created successfully with IDs: " + saleKeyHolder.getKey());
	        return true;
    	}catch (Exception e) {

    		LOGGER.error("Error creating user in the database. Message: " + e.getMessage());
    	}
		return false;
	}
	
	public Sale getSaleById(int saleId) {
		if(!this.existSale(saleId)) {
			String errorMessage = "ERROR 404. Sale with ID: { " + saleId + " } don't exist on the database";
            LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
			
		}
		StringBuffer saleSql= new StringBuffer("");
        saleSql.append("SELECT * FROM sales WHERE saleId = ?");
        final String saleQuery = saleSql.toString();
        
        try {
            @SuppressWarnings("deprecation")
			Sale sale = jdbcTemplate.queryForObject(saleQuery, new Object[]{saleId}, new SaleRowMapper());
            LOGGER.info("Sale retrieved succesfully with ID: { " + sale.getSaleId() + " }");
            return sale;
        } catch (Exception e) {
            LOGGER.error("Error retrieving sale from the database. Message " + e.getMessage());
        }
        return null;
	}
	public List<Sale> getSaleByCategory(String category){
		if(!this.verifyCategory(category)) {
			String errorMessage = "ERROR 404. Sale category must be one of the following: Food, Clothes, Electronics, Home, Health, Beauty, Automotive, Shoes, Other";
            LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
			
		}
		if(!this.existCategory(category)) {
			String errorMessage = "ERROR 400. Sale don't exist with category: { " + category + " }";
            LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
			
		}
        StringBuffer saleSql= new StringBuffer("");
        saleSql.append("SELECT * FROM sales WHERE category = ?");
        final String saleQuery = saleSql.toString();

        try {
            @SuppressWarnings("deprecation")
			List <Sale> sales = jdbcTemplate.query(saleQuery, new Object[]{category}, new SaleRowMapper());
            LOGGER.info("All sales retrieved succesfully");
            return sales;
        } catch (Exception e) {
            LOGGER.error("Error retrieving all the sales from the database. Message " + e.getMessage());
        }
        return null;
	}
	public List<Sale> getSaleByClient(int clientId){
		if(!this.existClient(clientId)){
			String errorMessage ="Error 404. Client with ID {"+ clientId+"} either does not exist.";
			LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
			
		}
        //if the client does not have sales
        if(!this.existSale(clientId)) {
        	String errorMessage ="Error 404. Client with ID {"+ clientId+"} does not have sales.";
            LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
        }
        StringBuffer saleSql= new StringBuffer("");
        saleSql.append("SELECT * FROM sales WHERE clientId = ?");
        final String saleQuery = saleSql.toString();

        try {
            @SuppressWarnings("deprecation")
			List <Sale> sales = jdbcTemplate.query(saleQuery, new Object[]{clientId}, new SaleRowMapper());
            LOGGER.info("All sales retrieved succesfully");
            return sales;
        } catch (Exception e) {
            LOGGER.error("Error retrieving all the sales from the database. Message " + e.getMessage());
        }
        return null;
	}
	public List<Sale> getSaleByDate(String date){
        //if the date does not have sales
        if(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sales WHERE date = ?", Integer.class, date) == 0) {
        	String errorMessage ="Error 404. Date {"+ date+"} does not have sales.";
            LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
        }
        StringBuffer saleSql= new StringBuffer("");
        saleSql.append("SELECT * FROM sales WHERE date = ?");
        final String saleQuery = saleSql.toString();

        try {
            @SuppressWarnings("deprecation")
			List <Sale> sales = jdbcTemplate.query(saleQuery, new Object[]{date}, new SaleRowMapper());
            LOGGER.info("All sales retrieved succesfully");
            return sales;
        } catch (Exception e) {
            LOGGER.error("Error retrieving all the sales from the database. Message " + e.getMessage());
        }
        return null;
	}
	public boolean updateSaleStatus(int saleId,String newStatus) {
		if(!this.existSale(saleId)) {
			String errorMessage = "ERROR 404. Sale with id: {" + saleId + "} does not exist on the database.";
            LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
		}
		
		StringBuffer saleSql = new StringBuffer("");
        saleSql.append("UPDATE sales SET  status = ? ");
        saleSql.append("WHERE saleId = ?");
        
        final String saleQuery = saleSql.toString();
        try {
        	 jdbcTemplate.update(saleQuery, newStatus,saleId);

            LOGGER.info("Sale with id: {" + saleId + "} updated.");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error updating sale on the database. Message: " + e.getMessage());
            return false;
        }
	
	}

	public boolean deleteSale(int saleId) {
		if(!this.existSale(saleId)) {
			String errorMessage = "ERROR 404. Sale with ID {"+saleId+"} either does not exist .";
			LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
		}
		
		StringBuffer saleSql= new StringBuffer("");
        saleSql.append("DELETE FROM sales WHERE saleId = ?");
        final String saleQuery = saleSql.toString();
        
        try {
        	jdbcTemplate.update(saleQuery, saleId);
            jdbcTemplate.update("DELETE FROM sales WHERE saleId = ?", saleId);
            LOGGER.info("Sale with id: {" + saleId + "} deleted succesfully from the database.");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error deleting sale from the database. Message " + e.getMessage());
        }


		return false;
	}

    public List <Sale> getAllSales() {
        //if there are no sales
        if(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sales", Integer.class) == 0) {
            String errorMessage ="Error 404. There are no sales.";
            LOGGER.error(errorMessage);
            throw new CustomSaleException(errorMessage);
        }
    	StringBuffer saleSql= new StringBuffer("");
        saleSql.append("SELECT * FROM sales");
        final String saleQuery = saleSql.toString();

        try {
            List <Sale> sales = jdbcTemplate.query(saleQuery, new SaleRowMapper());
            LOGGER.info("All sales retrieved succesfully");
            return sales;
        } catch (Exception e) {
            LOGGER.error("Error retrieving all the sales from the database. Message " + e.getMessage());
        }
        return null;
    }

}