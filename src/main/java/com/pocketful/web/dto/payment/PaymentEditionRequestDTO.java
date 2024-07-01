package com.pocketful.web.dto.payment;

import com.pocketful.enums.PaymentSelectionOption;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEditionRequestDTO {
    private float amount;
    private String description;
    private Boolean payed;
    private Boolean isExpense;
    private LocalDate deadlineAt;
    private Long paymentCategoryId;
    private PaymentSelectionOption type;
}
