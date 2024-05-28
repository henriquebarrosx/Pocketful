package com.pocketful.web.mapper;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.web.dto.payment_category.PaymentCategoryDTO;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PaymentCategoryDTOMapper implements Function<PaymentCategory, PaymentCategoryDTO> {
    @Override
    public PaymentCategoryDTO apply(PaymentCategory paymentCategory) {
        return PaymentCategoryDTO.builder()
                .id(paymentCategory.getId())
                .name(paymentCategory.getName())
                .build();
    }
}
