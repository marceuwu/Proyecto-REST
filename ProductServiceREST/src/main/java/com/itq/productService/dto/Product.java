package com.itq.productService.dto;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {

    private int productId;
    private int providerId;
    @NotBlank(message = "Product name is mandatory")
    @Length(max = 30, message = "Product name must be 30 characters or less")
    private String productName;
    @Min(value = 0, message = "Product price must be greater than 0")
    private double productPrice;
    @Min(value = 0, message = "Product stock must be greater than 0")
    @JsonProperty("stock")
    private int productStock;
    @NotBlank(message = "Product brand is mandatory")
    @Length(max = 30, message = "Product brand must be 30 characters or less")
    private String productBrand;
    @NotBlank(message = "Product category is mandatory")
    @Pattern(regexp="^(Food|Clothes|Electronics|Home|Health|Beauty|Automotive|Shoes|Other)$", message="Product category must be one of the following: Food, Clothes, Electronics, Home, Health, Beauty, Automotive, Shoes, Other")
    @JsonProperty("category")
    private String productCategory;
    @JsonProperty("characteristics")
    private List<Characteristics> productCharacteristics;
    
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getProviderId() {
		return providerId;
	}
	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public double getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}
	public int getProductStock() {
		return productStock;
	}
	public void setProductStock(int productStock) {
		this.productStock = productStock;
	}
	public String getProductBrand() {
		return productBrand;
	}
	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}
	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}
	public List<Characteristics> getProductCharacteristics() {
		return productCharacteristics;
	}
	public void setProductCharacteristics(List<Characteristics> productCharacteristics) {
		this.productCharacteristics = productCharacteristics;
	}
}
