package com.pocketful.exception.PaymentFrequency;

public class InvalidFrequencyTimesException extends RuntimeException {
    public InvalidFrequencyTimesException() {
        super("Invalid frequency times");
    }
}
