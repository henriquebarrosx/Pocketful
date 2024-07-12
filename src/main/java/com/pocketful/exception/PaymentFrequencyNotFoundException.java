package com.pocketful.exception;

public class PaymentFrequencyNotFoundException extends RuntimeException {
    public PaymentFrequencyNotFoundException(Long id) {
        super(String.format("Payment Frequency by id %s not found", id));
    }
}
