package com.pocketful.web.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NewPaymentDTO {
    private float amount;
    private String description;
    private Boolean payed;
    private Boolean isExpense;
    private LocalDate deadlineAt;
    private Integer frequencyTimes;
    private Boolean isIndeterminate;
    private Long accountId;
    private Long paymentCategoryId;
}
