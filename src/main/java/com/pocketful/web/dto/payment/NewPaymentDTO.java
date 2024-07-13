package com.pocketful.web.dto.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewPaymentDTO {
    private BigDecimal amount;
    private String description;
    private Boolean payed;
    private Boolean isExpense;
    private LocalDate deadlineAt;
    private Integer frequencyTimes;
    private Boolean isIndeterminate;
    private Long paymentCategoryId;
}
