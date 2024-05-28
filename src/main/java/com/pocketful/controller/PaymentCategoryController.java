package com.pocketful.controller;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.service.PaymentCategoryService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
