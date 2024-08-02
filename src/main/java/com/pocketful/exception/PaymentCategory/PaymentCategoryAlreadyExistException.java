package com.pocketful.exception.PaymentCategory;

public class PaymentCategoryAlreadyExistException extends RuntimeException {
    public PaymentCategoryAlreadyExistException(String name) {
        super(String.format("Payment category %s already exists.", name));
    }
}
