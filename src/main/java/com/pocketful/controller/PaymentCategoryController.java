package com.pocketful.controller;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.service.PaymentCategoryService;
import com.pocketful.web.dto.payment_category.NewPaymentCategoryDTO;
import com.pocketful.web.dto.payment_category.PaymentCategoryIdDTO;

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

    @GetMapping
    public ResponseEntity<List<PaymentCategory>> getAll() {
        List<PaymentCategory> paymentCategories = paymentCategoriesService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentCategories);
    }

    @PostMapping
    public ResponseEntity<PaymentCategoryIdDTO> create(@RequestBody NewPaymentCategoryDTO newPaymentCategoryDTO) {
        PaymentCategory paymentCategory = paymentCategoriesService
                .create(newPaymentCategoryDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new PaymentCategoryIdDTO(paymentCategory.getId()));
    }
}
