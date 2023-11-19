package com.itq.productService.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.itq.productService.business.ProductBusiness;
import com.itq.productService.dto.Ack;
import com.itq.productService.dto.Product;

@RestController
@Validated
public class ProductServiceController {

    @Autowired
    private ProductBusiness productBusiness;

    @PostMapping(value = "/product", consumes = "application/json", produces = "application/json")
    public Ack createProduct(@Valid @RequestBody Product product) {
        Ack ack = new Ack();
        if (productBusiness.createProduct(product)) {
            ack.setCode(200);
            ack.setDescription("Product created succesfully");
        } else {
            ack.setCode(400);
            ack.setDescription("Error creating product");
        }
        return ack;
    }

    @PutMapping(value = "/product/{productId}", consumes = "application/json", produces = "application/json")
    public Ack updateProduct(@PathVariable("productId") int productId, @Valid @RequestBody Product product) {
        Ack ack = new Ack();
        if (productBusiness.updateProduct(productId, product)) {
            ack.setCode(200);
            ack.setDescription("Product updated succesfully");
        } else {
            ack.setCode(400);
            ack.setDescription("Error updating product");
        }
        return ack;
    }

    @DeleteMapping(value = "/product/{providerId}/{productId}", produces = "application/json")
    public Ack deleteProduct(@PathVariable("providerId") int providerId, @PathVariable("productId") int productId) {
        Ack ack = new Ack();
        if (productBusiness.deleteProduct(productId, providerId)) {
            ack.setCode(200);
            ack.setDescription("Product deleted succesfully");
        } else {
            ack.setCode(400);
            ack.setDescription("Error deleting product");
        }
        return ack;
    }

    @GetMapping(value = "/product/{productId}", produces = "application/json")
    public Product getProductById(@PathVariable("productId") int productId) {
        return productBusiness.getProductById(productId);
    }

    @GetMapping(value = "/products")
    public List<Product> getAllProducts() {
        return (List<Product>) productBusiness.getAllProducts();
    }

    @GetMapping(value = "/products/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable("category") String category) {
        return (List<Product>) productBusiness.getProductsByCategory(category);
    }

    @GetMapping(value = "/products/provider/{userId}")
    public List<Product> getProductsByProviderId(@PathVariable("userId") int userId) {
        return (List<Product>) productBusiness.getProductsByProviderId(userId);
    }
    
}
