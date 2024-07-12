package com.pocketful.exception;

public class InvalidPaymentAmountException extends RuntimeException {
    public InvalidPaymentAmountException() {
        super("Amount should be greater or equals 0.");
    }
}
