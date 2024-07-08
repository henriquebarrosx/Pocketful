package com.pocketful.web.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class PaymentGenerationPayloadDTO {
    private Long id;
    private Float amount;
    private String description;
    private Boolean payed;
    private Boolean isExpense;
    private LocalDate deadlineAt;
    private Long accountId;
    private Long paymentCategoryId;
    private Long paymentFrequencyId;
}
