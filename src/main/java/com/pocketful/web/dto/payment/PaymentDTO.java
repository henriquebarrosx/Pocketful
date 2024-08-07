package com.pocketful.web.dto.payment;

import com.pocketful.web.dto.payment_category.PaymentCategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private BigDecimal amount;
    private String description;
    private boolean payed;
    private boolean isExpense;
    private int frequencyTimes;
    private String deadlineAt;
    private PaymentCategoryDTO paymentCategory;
}
