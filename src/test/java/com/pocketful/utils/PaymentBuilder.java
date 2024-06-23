package com.pocketful.utils;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.web.dto.payment.NewPaymentDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PaymentBuilder {
    public static Payment buildPayment() {
        return Payment.builder()
                .id(1L)
                .amount(10)
                .description("Uber")
                .payed(false)
                .isExpense(true)
                .deadlineAt(LocalDate.parse("2024-10-01"))
                .account(AccountBuilder.buildAccount())
                .paymentCategory(PaymentCategoryBuilder.buildPaymentCategory())
                .paymentFrequency(PaymentFrequencyBuilder.buildPaymentFrequency())
                .build();
    }

    public static Payment buildPayment(Account account, PaymentCategory paymentCategory, PaymentFrequency paymentFrequency) {
        return Payment.builder()
                .id(1L)
                .amount(10)
                .description("Uber")
                .payed(false)
                .isExpense(true)
                .deadlineAt(LocalDate.parse("2024-10-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .account(account)
                .paymentCategory(paymentCategory)
                .paymentFrequency(paymentFrequency)
                .build();
    }

    public static NewPaymentDTO buildNewPaymentRequest() {
        return NewPaymentDTO.builder()
                .amount(10)
                .description("Uber")
                .payed(false)
                .isExpense(true)
                .deadlineAt(LocalDate.parse("2024-10-01"))
                .accountId(1L)
                .paymentCategoryId(1L)
                .frequencyTimes(1)
                .isIndeterminate(false)
                .build();
    }

    public static NewPaymentDTO buildNewPaymentRequest(Account account) {
        return NewPaymentDTO.builder()
                .amount(10)
                .description("Uber")
                .payed(false)
                .isExpense(true)
                .deadlineAt(LocalDate.parse("2024-10-01"))
                .accountId(account.getId())
                .paymentCategoryId(1L)
                .frequencyTimes(1)
                .isIndeterminate(false)
                .build();
    }

    public static NewPaymentDTO buildNewPaymentRequest(Account account, PaymentCategory paymentCategory, float amount) {
        return NewPaymentDTO.builder()
                .amount(amount)
                .description("Uber")
                .payed(false)
                .isExpense(true)
                .deadlineAt(LocalDate.parse("2024-10-01"))
                .accountId(account.getId())
                .paymentCategoryId(paymentCategory.getId())
                .frequencyTimes(1)
                .isIndeterminate(false)
                .build();
    }

    public static NewPaymentDTO buildNewPaymentRequest(Account account, PaymentCategory paymentCategory, int frequencyTimes) {
        return NewPaymentDTO.builder()
                .amount(10)
                .description("Uber")
                .payed(false)
                .isExpense(true)
                .deadlineAt(LocalDate.parse("2024-10-01"))
                .accountId(account.getId())
                .paymentCategoryId(paymentCategory.getId())
                .frequencyTimes(frequencyTimes)
                .isIndeterminate(false)
                .build();
    }

    public static NewPaymentDTO buildNewPaymentRequest(Account account, PaymentCategory paymentCategory, boolean isIndeterminate) {
        return NewPaymentDTO.builder()
                .amount(10)
                .description("Uber")
                .payed(false)
                .isExpense(true)
                .deadlineAt(LocalDate.parse("2024-10-01"))
                .accountId(account.getId())
                .paymentCategoryId(paymentCategory.getId())
                .frequencyTimes(1)
                .isIndeterminate(isIndeterminate)
                .build();
    }
}
