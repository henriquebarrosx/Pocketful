package com.pocketful.exception.Payment;

public class InvalidPaymentAmountException extends RuntimeException {
    public InvalidPaymentAmountException() {
        super("Amount should be greater or equals 0.");
    }
}
