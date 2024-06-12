package com.pocketful.utils;

import com.pocketful.entity.PaymentCategory;

import java.time.LocalDate;

public class PaymentCategoryBuilder {
    public static PaymentCategory buildPaymentCategory() {
        return PaymentCategory.builder()
                .id(1L)
                .name("Saúde")
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
    }
}
