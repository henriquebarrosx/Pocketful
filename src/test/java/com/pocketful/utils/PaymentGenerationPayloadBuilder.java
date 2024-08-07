package com.pocketful.utils;

import com.pocketful.entity.Account;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.web.dto.payment.PaymentGenerationPayloadDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class PaymentGenerationPayloadBuilder {

    public static PaymentGenerationPayloadDTO build(Long id,
                                                    LocalDate deadline,
                                                    Account account,
                                                    PaymentCategory paymentCategory,
                                                    PaymentFrequency paymentFrequency) {
        return PaymentGenerationPayloadDTO.builder()
                .id(id)
                .paymentFrequencyId(paymentFrequency.getId())
                .paymentCategoryId(paymentCategory.getId())
                .accountId(account.getId())
                .deadlineAt(deadline)
                .description("Transport")
                .amount(BigDecimal.valueOf(1000))
                .payed(false)
                .isExpense(true)
                .build();
    }

}
