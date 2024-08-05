package com.pocketful.utils;

import com.pocketful.web.dto.payment.PaymentCreationRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class PaymentCreationRequestBuilder {
    public static PaymentCreationRequestDTO build() {
        return PaymentCreationRequestDTO.builder()
                .paymentCategoryId(1L)
                .build();
    }

    public static PaymentCreationRequestDTO buildWithAmount(BigDecimal amount) {
        return PaymentCreationRequestDTO.builder()
                .paymentCategoryId(1L)
                .amount(amount)
                .build();
    }

    public static PaymentCreationRequestDTO buildWithFrequency(Integer times) {
        return PaymentCreationRequestDTO.builder()
                .paymentCategoryId(1L)
                .amount(BigDecimal.valueOf(100))
                .isIndeterminate(false)
                .frequencyTimes(times)
                .payed(false)
                .description("Transport")
                .isExpense(true)
                .deadlineAt(LocalDate.now().plusDays(5))
                .build();
    }
}
