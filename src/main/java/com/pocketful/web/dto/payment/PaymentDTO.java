package com.pocketful.web.dto.payment;

import com.pocketful.web.dto.payment_category.PaymentCategoryDTO;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long id;
    private float amount;
    private String description;
    private boolean payed;
    private boolean isExpense;
    private int frequencyTimes;
    private LocalDate deadlineAt;
    private PaymentCategoryDTO paymentCategory;
}
