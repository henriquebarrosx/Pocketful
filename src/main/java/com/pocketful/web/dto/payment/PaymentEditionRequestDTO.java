package com.pocketful.web.dto.payment;

import com.pocketful.enums.PaymentSelectionOption;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEditionRequestDTO {
    private BigDecimal amount;
    private String description;
    private Boolean payed;
    private Boolean isExpense;
    private LocalDate deadlineAt;
    private Long paymentCategoryId;
    private PaymentSelectionOption type;
}
