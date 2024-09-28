package com.pocketful.model.dto.payment;

import com.pocketful.enums.PaymentSelectionOption;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEditionRequestDTO {
    @NotNull(message = "amount cannot be null")
    @PositiveOrZero(message = "amount must to be greater or equals 0.")
    private BigDecimal amount;

    @NotNull(message = "description cannot be null")
    @NotEmpty(message = "description cannot be empty")
    @NotBlank(message = "description cannot be blank")
    private String description;

    @NotNull(message = "payed cannot be null")
    private Boolean payed;

    @NotNull(message = "isExpense cannot be null")
    private Boolean isExpense;

    @NotNull(message = "deadlineAt cannot be null")
    private LocalDate deadlineAt;

    @NotNull(message = "paymentCategoryId cannot be null")
    private Long paymentCategoryId;

    @NotNull(message = "type cannot be null")
    private PaymentSelectionOption type;
}
