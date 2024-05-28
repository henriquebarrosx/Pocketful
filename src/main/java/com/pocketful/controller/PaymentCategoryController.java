package com.pocketful.controller;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.service.PaymentCategoryService;
import com.pocketful.web.mapper.PaymentCategoryDTOMapper;
import com.pocketful.web.dto.payment_category.PaymentCategoryDTO;
import com.pocketful.web.dto.payment_category.NewPaymentCategoryDTO;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RequestMapping("v1/payments/categories")
@RestController
public class PaymentCategoryController {
    private final PaymentCategoryService paymentCategoriesService;
    private final PaymentCategoryDTOMapper paymentCategoryDTOMapper;

    @GetMapping
    public ResponseEntity<List<PaymentCategoryDTO>> getAll() {
        List<PaymentCategoryDTO> paymentCategories = paymentCategoriesService.findAll().stream()
                .map(paymentCategoryDTOMapper)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentCategories);
    }

    @PostMapping
    public ResponseEntity<PaymentCategoryDTO> create(@RequestBody NewPaymentCategoryDTO newPaymentCategoryDTO) {
        PaymentCategory paymentCategory = paymentCategoriesService
                .create(newPaymentCategoryDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentCategoryDTOMapper.apply(paymentCategory));
    }

    @PutMapping("{id}")
    public ResponseEntity<PaymentCategoryDTO> update(
            @PathVariable Long id,
            @RequestBody NewPaymentCategoryDTO newPaymentCategoryDTO) {
        PaymentCategory paymentCategory = paymentCategoriesService
                .update(id, newPaymentCategoryDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentCategoryDTOMapper.apply(paymentCategory));
    }
}
