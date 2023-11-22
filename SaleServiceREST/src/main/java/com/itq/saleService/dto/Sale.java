package com.itq.saleService.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sale {
	private int saleId;
	private int productId;
	private int providerId;
	private int clientId;
	@Length(max = 30, message = "ERROR 404. Product name must be 30 characters or less")
	private String saleName;
	@PositiveOrZero(message = "ERROR 404. Product price must be greater than or equal to 0")
	private float productPrice;
	@NotBlank(message = "ERROR 404. Date is mandatory")
	@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "ERROR 404. Date should be in the format YYYY-MM-DD")
	private String date;
	@Min(value = 1, message = "ERROR 404. Product quantity must be greater than 0")
	private int productQuantity;
	private float total;
	@NotBlank(message = "ERROR 404. Status is mandatory")
	@Pattern(regexp = "^(Active|Inactive)$", message = "ERROR 404. Status must be either Active or Inactive")
	private String status;
	@Pattern(regexp="^(Food|Clothes|Electronics|Home|Health|Beauty|Automotive|Shoes|Other)$", message="ERROR 404. Sale category must be one of the following: Food, Clothes, Electronics, Home, Health, Beauty, Automotive, Shoes, Other")
    @JsonProperty("category")
    private String saleCategory;
	
	public int getSaleId() {
		return saleId;
	}
	public void setSaleId(int saleId) {
		this.saleId = saleId;
	}
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
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public String getSaleName() {
		return saleName;
	}
	public void setSaleName(String productName) {
		this.saleName = productName;
	}
	public float getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(float productPrice) {
		this.productPrice = productPrice;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getProductQuantity() {
		return productQuantity;
	}
	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}
	public float getTotal() {
		return total;
	}
	public void setTotal(float total) {
		this.total = total;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSaleCategory() {
		return saleCategory;
	}
	public void setSaleCategory(String saleCategory) {
		this.saleCategory = saleCategory;
	}
	
	

	

}
