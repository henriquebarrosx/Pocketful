package com.pocketful.utils;

import com.pocketful.entity.PaymentCategory;

import java.time.LocalDate;

public abstract class PaymentCategoryBuilder {

    public static PaymentCategory build() {
        return PaymentCategory.builder()
                .id(1L)
                .name("Transport")
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
    }

}
