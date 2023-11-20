package com.itq.auctionService.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class Auction {
	
	private int auctionId;
	private int productId;
	private int clientId;
	private int providerId;
	@Min(value = 0, message = "Initial price must be greater than 0")
	private double initialPrice;
	@Min(value = 0, message = "Final price must be greater than 0")
	private double finalPrice;
	@NotBlank(message = "Auction date is mandatory")
	@Pattern(regexp = "([0-9]{4})-([0-9]{2})-([0-9]{2})", message = "Auction date must be in the format yyyy-mm-dd")
	private String auctionDate;
	@NotBlank(message = "Auction status is mandatory")
	@Pattern(regexp = "^(Active|Inactive)$", message = "Auction status must be Active or Inactive")
	private String auctionStatus;

	public int getProviderId() {
		return providerId;
	}
	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}
	public int getAuctionId() {
		return auctionId;
	}
	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public double getInitialPrice() {
		return initialPrice;
	}
	public void setInitialPrice(double initialPrice) {
		this.initialPrice = initialPrice;
	}
	public double getFinalPrice() {
		return finalPrice;
	}
	public void setFinalPrice(double finalPrice) {
		this.finalPrice = finalPrice;
	}
	public String getAuctionDate() {
		return auctionDate;
	}
	public void setAuctionDate(String auctionDate) {
		this.auctionDate = auctionDate;
	}
	public String getAuctionStatus() {
		return auctionStatus;
	}
	public void setAuctionStatus(String auctionStatus) {
		this.auctionStatus = auctionStatus;
	}
	
}
