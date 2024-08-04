package com.pocketful.utils;

import com.pocketful.entity.Payment;

public abstract class PaymentBuilder {

    public static Payment build() {
        return Payment.builder()
                .id(1L)
                .build();
    }

}
