package com.pocketful.web.dto.payment;

import com.pocketful.enums.PaymentSelectionOption;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDeletionRequestDTO {
    @NotNull(message = "type cannot be null")
    private PaymentSelectionOption type;
}
