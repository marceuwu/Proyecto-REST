package com.itq.userService.dto;

import org.hibernate.validator.constraints.Length;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {
	
	private int addressID;
	@Length(max = 50, message = "Street must be less than 50 characters")
	private String street;
	@Length(max = 5, message = "ExtNumber must be less than 5 characters")
	private int extNumber;
	@Length(max = 5, message = "IntNumber must be less than 5 characters")
	private int intNumber;
	@JsonProperty("suburb")
	@Length(max = 50, message = "Locality must be less than 50 characters")
	private String locality;
	@Length(max = 10, message = "ZipCode must be less than 10 characters")
	private int zipCode;
	@Length(max = 25, message = "City must be less than 25 characters")
	private String city;
	@Length(max = 25, message = "State must be less than 25 characters")
	private String state;
	@Length(max = 25, message = "Country must be less than 25 characters")
	private String country;
	
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public int getExtNumber() {
		return extNumber;
	}
	public void setExtNumber(int extNumber) {
		this.extNumber = extNumber;
	}
	public int getIntNumber() {
		return intNumber;
	}
	public void setIntNumber(int intNumber) {
		this.intNumber = intNumber;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public int getZipCode() {
		return zipCode;
	}
	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getAddressID() {
		return addressID;
	}
	public void setAddressID(int addressID) {
		this.addressID = addressID;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
}
