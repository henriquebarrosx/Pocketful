package com.pocketful.exception;

public class PaymentCategoryNotFoundException extends RuntimeException {
    public PaymentCategoryNotFoundException(Long id) {
        super(String.format("Payment Category by id %s not found", id));
    }
}
