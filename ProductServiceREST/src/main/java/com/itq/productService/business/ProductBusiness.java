package com.itq.productService.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.itq.productService.dao.ProductDao;
import com.itq.productService.dto.Product;

@Repository
public class ProductBusiness {

    @Autowired
    private ProductDao productDao;

    public boolean createProduct(Product product) {
        return productDao.createProduct(product);
    }

    public Product getProductById(int productId) {
        return productDao.getProductById(productId);
    }

    public boolean updateProduct(int productId, Product product) {
        return productDao.updateProduct(productId, product);
    }

    public boolean deleteProduct(int productId, int providerId) {
        return productDao.deleteProduct(productId, providerId);
    }

    public List<Product> getAllProducts() {
        return (List<Product>) productDao.getAllProducts();
    }

    public List<Product> getProductsByCategory(String category) {
        return (List<Product>) productDao.getProductsByCategory(category);
    }

    public List<Product> getProductsByProviderId(int providerId) {
        return (List<Product>) productDao.getProductsByProviderId(providerId);
    }

}
