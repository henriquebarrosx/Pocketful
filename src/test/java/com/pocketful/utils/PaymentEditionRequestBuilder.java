package com.pocketful.utils;

import com.pocketful.web.dto.payment.PaymentEditionRequestDTO;

public abstract class PaymentEditionRequestBuilder {

    public static PaymentEditionRequestDTO build() {
        return PaymentEditionRequestDTO.builder()
            .build();
    }

}
