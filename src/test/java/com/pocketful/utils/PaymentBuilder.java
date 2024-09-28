package com.pocketful.utils;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.model.dto.payment.PaymentCreationRequestDTO;
import com.pocketful.model.dto.payment.PaymentEditionRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
                .updatedAt(LocalDateTime.of(2024, 8, 1, 12, 0, 0))
                .build();
    }

    public static Payment build(Account account,
                                PaymentCategory paymentCategory,
                                PaymentFrequency paymentFrequency) {

        return Payment.builder()
                .id(1L)
                .account(account)
                .paymentCategory(paymentCategory)
                .paymentFrequency(paymentFrequency)
                .amount(BigDecimal.valueOf(1000))
                .description("Exame médico")
                .deadlineAt(LocalDate.of(2024, 5, 12))
                .isExpense(true)
                .payed(false)
                .deadlineAt(LocalDate.of(2024, 8, 5))
                .updatedAt(LocalDateTime.of(2024, 8, 1, 12, 0, 0))
                .build();
    }

    public static Payment build(Long id,
                                LocalDate deadline,
                                Account account,
                                PaymentCategory paymentCategory,
                                PaymentFrequency paymentFrequency) {

        return Payment.builder()
                .id(id)
                .account(account)
                .paymentCategory(paymentCategory)
                .paymentFrequency(paymentFrequency)
                .amount(BigDecimal.valueOf(1000))
                .description("Exame médico")
                .deadlineAt(deadline)
                .isExpense(true)
                .payed(false)
                .deadlineAt(deadline)
                .updatedAt(LocalDateTime.of(2024, 8, 1, 12, 0, 0))
                .build();
    }
}
