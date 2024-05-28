package com.pocketful.service;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.exception.ConflictException;
import com.pocketful.exception.NotFoundException;
import com.pocketful.repository.PaymentCategoryRepository;
import com.pocketful.web.dto.payment_category.NewPaymentCategoryDTO;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class PaymentCategoryService {
    private final PaymentCategoryRepository paymentCategoryRepository;

    public List<PaymentCategory> findAll() {
        return paymentCategoryRepository.findAll();
    }

    public PaymentCategory create(NewPaymentCategoryDTO newPaymentCategoryDTO) {
        Boolean existsPaymentWithName = paymentCategoryRepository
                .existsPaymentCategoryByName(newPaymentCategoryDTO.getName());

        if (existsPaymentWithName) {
            throw new ConflictException("Payment category already exists.");
        }

        return paymentCategoryRepository.save(
                PaymentCategory.builder()
                        .name(newPaymentCategoryDTO.getName())
                        .build()
        );
    }

    public PaymentCategory update(Long id, NewPaymentCategoryDTO paymentCategoryDTO) {
        PaymentCategory paymentCategory = paymentCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment Category not found"));

        if (paymentCategory.getName().equals(paymentCategoryDTO.getName())) {
            throw new ConflictException("Payment category already registered as " + paymentCategoryDTO.getName());
        }

        Boolean existsPaymentWithName = paymentCategoryRepository
                .existsPaymentCategoryByName(paymentCategoryDTO.getName());

        if (existsPaymentWithName) {
            throw new ConflictException("Payment category already exists.");
        }

        paymentCategory.setName(paymentCategoryDTO.getName());
        return paymentCategoryRepository.save(paymentCategory);
    }
}
