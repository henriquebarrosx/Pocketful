package com.pocketful.mapper;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.web.dto.payment_category.PaymentCategoryDTO;
import org.springframework.stereotype.Service;

@Service
public class PaymentCategoryMapper {
    public PaymentCategoryDTO toDTO(PaymentCategory paymentCategory) {
        return PaymentCategoryDTO.builder()
                .id(paymentCategory.getId())
                .name(paymentCategory.getName())
                .build();
    }
}
