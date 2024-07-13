package com.pocketful.web.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class PaymentGenerationPayloadDTO {
    private Long id;
    private BigDecimal amount;
    private String description;
    private Boolean payed;
    private Boolean isExpense;
    private LocalDate deadlineAt;
    private Long accountId;
    private Long paymentCategoryId;
    private Long paymentFrequencyId;
}
