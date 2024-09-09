package com.pocketful.exception.PaymentFrequency;

public class PaymentFrequencyNotFoundException extends RuntimeException {
    public PaymentFrequencyNotFoundException() {
        super("Payment Frequency not found");
    }
}
