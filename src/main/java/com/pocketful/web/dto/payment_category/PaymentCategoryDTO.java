package com.pocketful.web.dto.payment_category;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class PaymentCategoryDTO {
    private Long id;
    private String name;
}
