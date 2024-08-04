package com.pocketful.web.mapper;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.web.dto.payment_category.PaymentCategoryDTO;
import org.springframework.stereotype.Service;

@Service
public abstract class PaymentCategoryDTOMapper {
    public static PaymentCategoryDTO apply(PaymentCategory paymentCategory) {
        return PaymentCategoryDTO.builder()
                .id(paymentCategory.getId())
                .name(paymentCategory.getName())
                .build();
    }
}
