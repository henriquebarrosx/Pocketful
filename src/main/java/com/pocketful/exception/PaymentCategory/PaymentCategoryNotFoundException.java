package com.pocketful.exception.PaymentCategory;

public class PaymentCategoryNotFoundException extends RuntimeException {
    public PaymentCategoryNotFoundException() {
        super("Payment Category not found");
    }
}
