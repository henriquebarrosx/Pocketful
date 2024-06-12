package com.pocketful.utils;

import com.pocketful.entity.PaymentFrequency;

import java.time.LocalDate;

public class PaymentFrequencyBuilder {
    public static PaymentFrequency buildPaymentFrequency() {
        return PaymentFrequency.builder()
                .id(1L)
                .times(1)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
    }
}
