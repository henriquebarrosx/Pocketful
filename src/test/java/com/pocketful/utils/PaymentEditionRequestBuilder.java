package com.pocketful.utils;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.model.dto.payment.PaymentEditionRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class PaymentEditionRequestBuilder {

    public static PaymentEditionRequestDTO build() {
        return PaymentEditionRequestDTO.builder()
                .paymentCategoryId(PaymentCategoryBuilder.build().getId())
                .amount(BigDecimal.valueOf(100))
                .type(PaymentSelectionOption.THIS_PAYMENT)
                .isExpense(true)
                .description("Lorem Ipsum")
                .payed(false)
                .deadlineAt(LocalDate.now())
                .build();
    }

    public static PaymentEditionRequestDTO buildWithCategory(PaymentCategory category) {
        return PaymentEditionRequestDTO.builder()
                .paymentCategoryId(category.getId())
                .build();
    }

    public static PaymentEditionRequestDTO buildWithAmount(BigDecimal amount) {
        return PaymentEditionRequestDTO.builder()
                .paymentCategoryId(PaymentCategoryBuilder.build().getId())
                .amount(amount)
                .build();
    }
}
