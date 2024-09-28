package com.pocketful.controller;

import com.pocketful.entity.Account;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.service.PaymentCategoryService;
import com.pocketful.util.SessionContext;
import com.pocketful.web.dto.payment_category.PaymentCategoryCreationRequestDTO;
import com.pocketful.web.dto.payment_category.PaymentCategoryDTO;
import com.pocketful.mapper.PaymentCategoryMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("v1/payments/categories")
@RestController
public class PaymentCategoryController {
    private final PaymentCategoryService paymentCategoriesService;
    private final PaymentCategoryMapper paymentCategoryMapper;

    @GetMapping
    public ResponseEntity<List<PaymentCategoryDTO>> getAll() {
        Account account = SessionContext.get();
        log.info("Getting payments categories: account id - {}", account.getId());

        List<PaymentCategoryDTO> categories = paymentCategoriesService.findAll().stream()
                .map(paymentCategoryMapper::toDTO)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("{id}")
    public ResponseEntity<PaymentCategoryDTO> getById(@PathVariable Long id) {
        Account account = SessionContext.get();
        log.info("Getting payment category by id: account id - {} | id = {}", id, account.getId());

        PaymentCategory category = paymentCategoriesService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(paymentCategoryMapper.toDTO(category));
    }

    @PostMapping
    public ResponseEntity<PaymentCategoryDTO> create(@RequestBody @Validated PaymentCategoryCreationRequestDTO request) {
        Account account = SessionContext.get();
        log.info("Creating payment category: account id - {} | name - {}", account.getId(), request.getName());

        PaymentCategory category = paymentCategoriesService.create(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCategoryMapper.toDTO(category));
    }

    @PutMapping("{id}")
    public ResponseEntity<PaymentCategoryDTO> update(
            @PathVariable Long id,
            @RequestBody @Validated PaymentCategoryCreationRequestDTO request) {
        Account account = SessionContext.get();
        log.info("Updating payment category: account id - {} | name - {}", account.getId(), request.getName());

        PaymentCategory category = paymentCategoriesService.update(id, request.getName());
        return ResponseEntity.status(HttpStatus.OK).body(paymentCategoryMapper.toDTO(category));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Account account = SessionContext.get();
        log.info("Deleting payment category: account id - {} | id - {}", account.getId(), id);

        paymentCategoriesService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
