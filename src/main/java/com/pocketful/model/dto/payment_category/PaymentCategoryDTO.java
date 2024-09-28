package com.pocketful.model.dto.payment_category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentCategoryDTO {
    private Long id;
    private String name;
}
