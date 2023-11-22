package com.itq.saleService.service;

@SuppressWarnings("serial")
public class CustomSaleException extends RuntimeException {

    public CustomSaleException(String message) {
        super(message);
    }
}