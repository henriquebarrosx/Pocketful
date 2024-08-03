package com.pocketful.utils;

import com.pocketful.web.dto.payment.PaymentCreationRequestDTO;

public abstract class PaymentCreationRequestBuilder {
    public static PaymentCreationRequestDTO build() {
        return PaymentCreationRequestDTO.builder()
            .build();
    }
}
