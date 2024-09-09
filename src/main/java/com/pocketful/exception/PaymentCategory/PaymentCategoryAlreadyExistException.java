package com.pocketful.exception.PaymentCategory;

public class PaymentCategoryAlreadyExistException extends RuntimeException {
    public PaymentCategoryAlreadyExistException() {
        super("Payment category already registered");
    }
}
