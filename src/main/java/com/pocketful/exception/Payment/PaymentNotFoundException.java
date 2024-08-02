package com.pocketful.exception.Payment;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long id) {
        super(String.format("Payment by id %s not found", id));
    }
}
