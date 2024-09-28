package com.pocketful.model.dto.payment;

import com.pocketful.model.dto.payment_category.PaymentCategoryDTO;
import lombok.*;

import java.math.BigDecimal;

@Data
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
