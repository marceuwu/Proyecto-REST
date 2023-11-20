package com.itq.auctionService.service;

@SuppressWarnings("serial")
public class CustomAuctionException extends RuntimeException {

    public CustomAuctionException(String message) {
        super(message);
    }
}
