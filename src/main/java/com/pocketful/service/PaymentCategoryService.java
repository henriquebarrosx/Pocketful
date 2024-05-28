package com.pocketful.service;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.exception.ConflictException;
import com.pocketful.repository.PaymentCategoryRepository;
import com.pocketful.web.dto.payment_category.NewPaymentCategoryDTO;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;

@AllArgsConstructor
@Service
public class PaymentCategoryService {
    private final PaymentCategoryRepository paymentCategoryRepository;

    public List<PaymentCategory> findAll() {
        return paymentCategoryRepository.findAll();
    }

    public PaymentCategory create(NewPaymentCategoryDTO newPaymentCategoryDTO) {
        Boolean existsPaymentWithName = paymentCategoryRepository
                .existsPaymentCategoriesByName(newPaymentCategoryDTO.getName());

        if (existsPaymentWithName) {
            throw new ConflictException("Payment category already exists.");
        }

        return paymentCategoryRepository.save(
                PaymentCategory.builder()
                        .name(newPaymentCategoryDTO.getName())
                        .createdAt(LocalDate.now())
                        .updatedAt(LocalDate.now())
                        .build()
        );
    }
}
