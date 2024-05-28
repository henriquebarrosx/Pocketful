package com.pocketful.web.dto.payment_category;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCategoryDTO {
    private Long id;
    private String name;
}
