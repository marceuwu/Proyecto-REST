package com.itq.productService.service;

@SuppressWarnings("serial")
public class CustomProductException extends RuntimeException {
    public CustomProductException(String message) {
        super(message);
    }
}