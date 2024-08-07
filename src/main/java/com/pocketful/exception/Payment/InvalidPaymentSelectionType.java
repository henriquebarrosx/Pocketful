package com.pocketful.exception.Payment;

public class InvalidPaymentSelectionType extends RuntimeException {
    public InvalidPaymentSelectionType() {
        super("Invalid payment selection type");
    }
}
