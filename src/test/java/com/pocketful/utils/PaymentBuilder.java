package com.pocketful.utils;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.web.dto.payment.PaymentCreationRequestDTO;
import com.pocketful.web.dto.payment.PaymentEditionRequestDTO;

import java.math.BigDecimal;

public abstract class PaymentBuilder {

    public static Payment build() {
        return Payment.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .description("Transport")
                .isExpense(true)
                .payed(false)
                .build();
    }

    public static Payment build(Account account,
                                PaymentCreationRequestDTO paymentParams,
                                PaymentCategory paymentCategory,
                                PaymentFrequency paymentFrequency) {

        return Payment.builder()
                .id(1L)
                .account(account)
                .paymentCategory(paymentCategory)
                .paymentFrequency(paymentFrequency)
                .amount(paymentParams.getAmount())
                .description(paymentParams.getDescription())
                .isExpense(paymentParams.getIsExpense())
                .payed(paymentParams.getPayed())
                .deadlineAt(paymentParams.getDeadlineAt())
                .build();
    }

    public static Payment build(Account account,
                                PaymentEditionRequestDTO paymentParams,
                                PaymentCategory paymentCategory,
                                PaymentFrequency paymentFrequency) {

        return Payment.builder()
                .id(1L)
                .account(account)
                .paymentCategory(paymentCategory)
                .paymentFrequency(paymentFrequency)
                .amount(paymentParams.getAmount())
                .description(paymentParams.getDescription())
                .isExpense(paymentParams.getIsExpense())
                .payed(paymentParams.getPayed())
                .deadlineAt(paymentParams.getDeadlineAt())
                .build();
    }
}
