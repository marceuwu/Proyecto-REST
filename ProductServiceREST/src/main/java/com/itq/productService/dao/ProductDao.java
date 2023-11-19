package com.itq.productService.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.itq.productService.dto.Product;

@Repository
public class ProductDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isProviderUser(int userId) {
        String sql = "SELECT COUNT(*) FROM user WHERE userId = ? AND type = 'Provider'";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count > 0;
    }

    public class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setProductId(rs.getInt("productId"));
            product.setProviderId(rs.getInt("providerId"));
            product.setProductName(rs.getString("productName"));
            product.setProductPrice(rs.getDouble("productPrice"));
            product.setProductStock(rs.getInt("stock"));
            product.setProductBrand(rs.getString("productBrand"));
            product.setProductCategory(rs.getString("category"));
            return product;
        }
    }

    public boolean createProduct(final Product product) {
        int userId = product.getProviderId();

        // Verify that the user exists and is a Provider
        if (!isProviderUser(userId)) {
            LOGGER.error("Error creating product: User with ID {} either does not exist or is not a Provider.", userId);
            return false;
        }
        StringBuffer productSql= new StringBuffer("");
        productSql.append("INSERT INTO product (productId, providerId, productName, productPrice, stock, productBrand, category) ");
        productSql.append("VALUES (?, ?, ?, ?, ?, ?, ?)");
        final String productQuery = productSql.toString();

        try {
            GeneratedKeyHolder productKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(productQuery, java.sql.Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, product.getProductId());
                    ps.setInt(2, userId);
                    ps.setString(3, product.getProductName());
                    ps.setDouble(4, product.getProductPrice());
                    ps.setInt(5, product.getProductStock());
                    ps.setString(6, product.getProductBrand());
                    ps.setString(7, product.getProductCategory());
                    return ps;
                }
            }, productKeyHolder);
            LOGGER.info("Product created succesfully with ID: { " + productKeyHolder.getKey().intValue() + " }");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error creating product in the database. Message " + e.getMessage());
        }
        return false;
    }

    public boolean updateProduct(int productId, Product product) {
        int userId = product.getProviderId();

        try {
            // Verify that the user exists and is a Provider
            if (!isProviderUser(userId)) {
                LOGGER.error("Error updating product: User with ID {} either does not exist or is not a Provider.", userId);
                return false;
            }

            Product existingProduct = getProductById(productId);

            // Verify that the product exists
            if (existingProduct == null) {
                LOGGER.error("Error updating product: Product with ID {} does not exist.", productId);
                return false;
            }

            StringBuffer productSql= new StringBuffer("");
            productSql.append("UPDATE product SET productName = ?, productPrice = ?, stock = ?, productBrand = ?, category = ? ");
            productSql.append("WHERE productId = ? AND providerId = ?");
            final String productQuery = productSql.toString();
        
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(productQuery);
                ps.setString(1, product.getProductName());
                ps.setDouble(2, product.getProductPrice());
                ps.setInt(3, product.getProductStock());
                ps.setString(4, product.getProductBrand());
                ps.setString(5, product.getProductCategory());
                ps.setInt(6, productId);
                ps.setInt(7, userId);
                return ps;
            });

            LOGGER.info("Product updated succesfully with ID: { " + productId + " }");
            return true;
        } catch (DataAccessException e) {
            LOGGER.error("Error updating product with ID {} in the database. Message {}", productId, e.getMessage());
            e.printStackTrace();
        }
		return false;
    }

    public Product getProductById(int productId) {
        StringBuffer productSql= new StringBuffer("");
        productSql.append("SELECT * FROM product WHERE productId = ?");
        final String productQuery = productSql.toString();

        try {
            @SuppressWarnings("deprecation")
			Product product = jdbcTemplate.queryForObject(productQuery, new Object[]{productId}, new ProductRowMapper());
            LOGGER.info("Product retrieved succesfully with ID: { " + product.getProductId() + " }");
            return product;
        } catch (Exception e) {
            LOGGER.error("Error retrieving product from the database. Message " + e.getMessage());
        }
        return null;
    }

    public boolean deleteProduct(int productId, int userId) {
        // Verify that the user exists and is a Provider
        if (!isProviderUser(userId)) {
            LOGGER.error("Error deleting product: User with ID {} either does not exist or is not a Provider.", userId);
            return false;
        }
        StringBuffer productSql= new StringBuffer("");
        productSql.append("DELETE FROM product WHERE productId = ? AND providerId = ?");
        final String productQuery = productSql.toString();

        try {
            int rowsAffected = jdbcTemplate.update(productQuery, productId, userId);
            if (rowsAffected == 0) {
                LOGGER.error("Error deleting product: Product with ID {} does not exist.", productId);
                return false;
            }
            LOGGER.info("Product deleted succesfully with ID: { " + productId + " }");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error deleting product from the database. Message " + e.getMessage());
        }
        return false;
    }

    public List <Product> getAllProducts() {
        StringBuffer productSql= new StringBuffer("");
        productSql.append("SELECT * FROM product");
        final String productQuery = productSql.toString();

        try {
            List <Product> products = jdbcTemplate.query(productQuery, new ProductRowMapper());
            LOGGER.info("All products retrieved succesfully");
            return products;
        } catch (Exception e) {
            LOGGER.error("Error retrieving all the products from the database. Message " + e.getMessage());
        }
        return null;
    }

    public List <Product> getProductsByCategory(String category) {
        StringBuffer productSql= new StringBuffer("");
        productSql.append("SELECT * FROM product WHERE category = ?");
        final String productQuery = productSql.toString();

        try {
            @SuppressWarnings("deprecation")
			List <Product> products = jdbcTemplate.query(productQuery, new Object[]{category}, new ProductRowMapper());
            LOGGER.info("All products retrieved succesfully");
            return products;
        } catch (Exception e) {
            LOGGER.error("Error retrieving all the products from the database. Message " + e.getMessage());
        }
        return null;
    }

    public List <Product> getProductsByProviderId(int userId) {
        // Verify that the user exists and is a Provider
        if (!isProviderUser(userId)) {
            LOGGER.error("Error retrieving products: User with ID {} either does not exist or is not a Provider.", userId);
            return null;
        }
        StringBuffer productSql= new StringBuffer("");
        productSql.append("SELECT * FROM product WHERE providerId = ?");
        final String productQuery = productSql.toString();

        try {
            @SuppressWarnings("deprecation")
			List <Product> products = jdbcTemplate.query(productQuery, new Object[]{userId}, new ProductRowMapper());
            LOGGER.info("All products retrieved succesfully");
            return products;
        } catch (Exception e) {
            LOGGER.error("Error retrieving all the products from the database. Message " + e.getMessage());
        }
        return null;
    }

}
