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
        PaymentCategory paymentCategory = findById(id);

        Boolean existsPaymentCategoryByName = paymentCategoryRepository
                .existsPaymentCategoryByName(paymentCategoryDTO.getName());

        if (existsPaymentCategoryByName) {
            throw new ConflictException("Payment category already exists.");
        }

        paymentCategory.setName(paymentCategoryDTO.getName());
        return paymentCategoryRepository.save(paymentCategory);
    }

    public void delete(Long id) {
        PaymentCategory paymentCategory = findById(id);
        paymentCategoryRepository.deleteById(paymentCategory.getId());
    }

    public PaymentCategory findById(Long id) {
        return paymentCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment Category not found"));
    }
}
