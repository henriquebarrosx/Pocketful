package com.pocketful.service;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.exception.ConflictException;
import com.pocketful.exception.NotFoundException;
import com.pocketful.repository.PaymentCategoryRepository;
import com.pocketful.web.dto.payment_category.NewPaymentCategoryDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentCategoryService {
    private final PaymentCategoryRepository paymentCategoryRepository;

    public List<PaymentCategory> findAll() {
        return paymentCategoryRepository.findAll();
    }

    public PaymentCategory create(NewPaymentCategoryDTO request) {
        Boolean existsPaymentWithName = paymentCategoryRepository
                .existsPaymentCategoryByName(request.getName());

        if (existsPaymentWithName) {
            log.error("Failed creating existent payment category: name - {}", request.getName());
            throw new ConflictException("Payment category already exists.");
        }

        return paymentCategoryRepository.save(
                PaymentCategory.builder()
                        .name(request.getName())
                        .build()
        );
    }

    public PaymentCategory update(Long id, NewPaymentCategoryDTO request) {
        PaymentCategory paymentCategory = findById(id);

        Boolean existsPaymentCategoryByName = paymentCategoryRepository
                .existsPaymentCategoryByName(request.getName());

        if (existsPaymentCategoryByName) {
            log.error("Failed updating existent payment category: name - {}", request.getName());
            throw new ConflictException("Payment category already exists.");
        }

        paymentCategory.setName(request.getName());
        return paymentCategoryRepository.save(paymentCategory);
    }

    public void delete(Long id) {
        PaymentCategory paymentCategory = findById(id);
        paymentCategoryRepository.deleteById(paymentCategory.getId());
    }

    public PaymentCategory findById(Long id) {
        return paymentCategoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Payment category not found: id - {}", id);
                    return new NotFoundException("Payment Category not found");
                });
    }
}
