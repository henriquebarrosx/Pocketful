package com.pocketful.model.dto.payment;

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
public class PaymentCreationRequestDTO {
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

    @NotNull(message = "frequencyTimes cannot be null")
    @PositiveOrZero(message = "frequencyTimes must to be greater or equals 0.")
    private Integer frequencyTimes;

    @NotNull(message = "isIndeterminate cannot be null")
    private Boolean isIndeterminate;

    @NotNull(message = "paymentCategoryId cannot be null")
    private Long paymentCategoryId;
}
