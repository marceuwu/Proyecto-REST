package com.itq.userService.dao;

import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.itq.userService.dto.Address;
import com.itq.userService.dto.User;

@Repository
public class UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);
    private int auxiliarAddressId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean deleteAddress(int addressId) {
        StringBuffer sql = new StringBuffer("");
        sql.append("DELETE FROM address WHERE addressId = ?");
        final String query = sql.toString();
        try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setInt(1, addressId);
                    return ps;
                }
            });
            //LOGGER.info("Address with ID {" + addressId + "} deleted successfully");
            return true;
        } catch (Exception e) {
            //LOGGER.error("Error deleting address with ID {" + addressId + "} from the database. Message: " + e.getMessage());
        }
        return false;
    }

    public boolean createUser(final User user) {

        // Step 1: Insert data into the 'address' table
        Address address = user.getAddress();
        StringBuffer addressSql = new StringBuffer("");
        addressSql.append("INSERT INTO address (addressId, street, exteriorNumber, interiorNumber, suburb, city, state, country, zipCode) ");
        addressSql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        final String addressQuery = addressSql.toString();
        final int addressId;

        try {
            GeneratedKeyHolder addressKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(addressQuery, java.sql.Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, address.getAddressID());
                    ps.setString(2, address.getStreet());
                    ps.setInt(3, address.getExtNumber());
                    ps.setInt(4, address.getIntNumber());
                    ps.setString(5, address.getLocality());
                    ps.setString(6, address.getCity());
                    ps.setString(7, address.getState());
                    ps.setString(8, "Mexico");
                    ps.setInt(9, address.getZipCode());
                    return ps;
                }
            }, addressKeyHolder);
            addressId = addressKeyHolder.getKey().intValue();
            auxiliarAddressId = addressId;

        // Step 2: Insert data into the 'user' table
        StringBuffer userSql = new StringBuffer("");
        userSql.append("INSERT INTO user (userId, name, lastname, addressId, phone, email, rfc, password, type) ");
        userSql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        final String userQuery = userSql.toString();

        GeneratedKeyHolder userKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(userQuery, java.sql.Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, user.getUserID());
                    ps.setString(2, user.getName());
                    ps.setString(3, user.getLastname());
                    ps.setInt(4, addressId);
                    ps.setString(5, user.getPhone());
                    ps.setString(6, user.getMail());
                    ps.setString(7, user.getRfc());
                    ps.setString(8, user.getPassword());
                    ps.setString(9, user.getType());
                    return ps;
                }
            }, userKeyHolder);
            LOGGER.info("User and Address created successfully with IDs: " + userKeyHolder.getKey() + ", " + addressId);
            return true;
        } catch (Exception e) {
            //Delete address if user creation fails, avoiding duplicate addresses, not the best solution but it works
            deleteAddress(auxiliarAddressId);
            LOGGER.error("Error creating user and address in the database. Message: " + e.getMessage());
        }
        return false;
    }

}
