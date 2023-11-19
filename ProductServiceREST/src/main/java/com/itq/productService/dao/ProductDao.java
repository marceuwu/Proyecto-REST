package com.itq.productService.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.itq.productService.dto.Characteristics;
import com.itq.productService.dto.Product;

@Repository
public class ProductDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isProviderUser(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE userId = ? AND type = 'Provider'";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count > 0;
    }

    public class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setProductId(rs.getInt("productId"));
            product.setProductName(rs.getString("productName"));
            product.setProductPrice(rs.getDouble("productPrice"));
            product.setProductStock(rs.getInt("stock"));
            product.setProductBrand(rs.getString("productBrand"));
            product.setProductCategory(rs.getString("category"));
            product.setProviderId(rs.getInt("providerId"));

            String characteristicSql = "SELECT * FROM characteristics WHERE productId = ?";
            @SuppressWarnings("deprecation")
			List<Characteristics> characteristics = jdbcTemplate.query(characteristicSql, new Object[]{product.getProductId()}, new RowMapper<Characteristics>() {
                @Override
                public Characteristics mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Characteristics characteristic = new Characteristics();
                    characteristic.setName(rs.getString("characteristicName"));
                    characteristic.setValue(rs.getString("characteristicValue"));
                    return characteristic;
                }
            });
            product.setProductCharacteristics(characteristics);
            return product;
        }
    }

    private void updateCharacteristics(int productId, List<Characteristics> characteristicsList) {
        // Delete existing characteristics for the product
        String deleteCharacteristicsSql = "DELETE FROM characteristics WHERE productId = ?";
        jdbcTemplate.update(deleteCharacteristicsSql, productId);
    
        // Insert new characteristics
        if (characteristicsList != null && !characteristicsList.isEmpty()) {
            for (Characteristics characteristic : characteristicsList) {
                insertCharacteristic(productId, characteristic);
            }
        }
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
            productSql.append("UPDATE products SET productName = ?, productPrice = ?, stock = ?, productBrand = ?, category = ? ");
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
            // Update or insert characteristics
            updateCharacteristics(productId, product.getProductCharacteristics());

            LOGGER.info("Product updated successfully with ID: { " + productId + " }");
            return true;
        } catch (DataAccessException e) {
            LOGGER.error("Error updating product with ID {} in the database. Message {}", productId, e.getMessage());
            e.printStackTrace();
        }
		return false;
    }

    public Product getProductById(int productId) {
        StringBuffer productSql= new StringBuffer("");
        productSql.append("SELECT * FROM products WHERE productId = ?");
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
        productSql.append("DELETE FROM products WHERE productId = ? AND providerId = ?");
        final String productQuery = productSql.toString();

        //Also delete the characteristics of the product
        String characteristicSql = "DELETE FROM characteristics WHERE productId = ?";
        jdbcTemplate.update(characteristicSql, productId);
        LOGGER.info("Characteristics deleted succesfully for product with ID: { " + productId + " }");

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
        productSql.append("SELECT * FROM products");
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
        productSql.append("SELECT * FROM products WHERE category = ?");
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
        productSql.append("SELECT * FROM products WHERE providerId = ?");
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

    
    public boolean createProduct(final Product product) {
        int userId = product.getProviderId();

        // Verify that the user exists and is a Provider
        if (!isProviderUser(userId)) {
            LOGGER.error("Error creating product: User with ID {} either does not exist or is not a Provider.", userId);
            return false;
        }

        // Insert product
        StringBuffer productSql = new StringBuffer("");
        productSql.append("INSERT INTO products (productId, productName, productPrice, stock, productBrand, category, providerId) ");
        productSql.append("VALUES (?, ?, ?, ?, ?, ?, ?)");
        final String productQuery = productSql.toString();

        try {
            GeneratedKeyHolder productKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(productQuery, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, product.getProductId());
                ps.setString(2, product.getProductName());
                ps.setDouble(3, product.getProductPrice());
                ps.setInt(4, product.getProductStock());
                ps.setString(5, product.getProductBrand());
                ps.setString(6, product.getProductCategory());
                ps.setInt(7, userId);
                return ps;
            }, productKeyHolder);

            int productId = Objects.requireNonNull(productKeyHolder.getKey()).intValue();

            // Insert characteristics
            List<Characteristics> characteristicsList = product.getProductCharacteristics();
            if (characteristicsList != null && !characteristicsList.isEmpty()) {
                for (Characteristics characteristic : characteristicsList) {
                    insertCharacteristic(productId, characteristic);
                }
            }

            LOGGER.info("Product created successfully with ID: { " + productId + " }");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error creating product in the database. Message " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void insertCharacteristic(int productId, Characteristics characteristic) {
        String characteristicSql = "INSERT INTO characteristics (characteristicName, characteristicValue, productId) VALUES (?, ?, ?)";
        jdbcTemplate.update(characteristicSql, characteristic.getName(), characteristic.getValue(), productId);
    }

}
