package com.pocketful.service;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.exception.PaymentCategory.PaymentCategoryAlreadyExistException;
import com.pocketful.exception.PaymentCategory.PaymentCategoryNotFoundException;
import com.pocketful.repository.PaymentCategoryRepository;
import com.pocketful.web.dto.payment_category.PaymentCategoryCreationRequestDTO;
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

    public PaymentCategory create(String name) {
        if (paymentCategoryRepository.existsPaymentCategoryByName(name)) {
            throw new PaymentCategoryAlreadyExistException(name);
        }

        PaymentCategory category = PaymentCategory.builder().name(name).build();
        return paymentCategoryRepository.save(category);
    }

    public PaymentCategory update(Long id, PaymentCategoryCreationRequestDTO request) {
        PaymentCategory paymentCategory = findById(id);

        Boolean existsPaymentCategoryByName = paymentCategoryRepository
                .existsPaymentCategoryByName(request.getName());

        if (existsPaymentCategoryByName) {
            throw new PaymentCategoryAlreadyExistException(request.getName());
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
                .orElseThrow(() -> new PaymentCategoryNotFoundException(id));
    }
}
