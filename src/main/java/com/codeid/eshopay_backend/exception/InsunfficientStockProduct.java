package com.codeid.eshopay_backend.exception;

public class InsunfficientStockProduct extends RuntimeException {

    public InsunfficientStockProduct(String message) {
        super(message);
    }
}
