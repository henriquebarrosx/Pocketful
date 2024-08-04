package com.pocketful.utils;

import com.pocketful.entity.PaymentFrequency;

import java.time.LocalDate;

public abstract class PaymentFrequencyBuilder {

    public static PaymentFrequency build() {
        return PaymentFrequency.builder()
                .id(1L)
                .times(3)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
    }

    public static PaymentFrequency build(Integer times) {
        return PaymentFrequency.builder()
                .id(1L)
                .times(times)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
    }

}
