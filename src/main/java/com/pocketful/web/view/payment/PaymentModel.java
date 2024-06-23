package com.pocketful.web.view.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PaymentModel {
    private String description;
    private String deadlineAt;
    private String amount;
    private boolean isOverdue;
}
