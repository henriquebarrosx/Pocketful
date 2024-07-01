package com.pocketful.web.dto.payment;

import com.pocketful.entity.Payment;
import com.pocketful.enums.PaymentSelectionOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentEditionQueuePayload {
    private Payment payment;
    private PaymentSelectionOption type;
}
