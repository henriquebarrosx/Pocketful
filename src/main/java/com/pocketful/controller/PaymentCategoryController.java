package com.pocketful.controller;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.service.PaymentCategoryService;
import com.pocketful.web.dto.payment_category.PaymentCategoryCreationRequestDTO;
import com.pocketful.web.dto.payment_category.PaymentCategoryDTO;
import com.pocketful.web.mapper.PaymentCategoryDTOMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("v1/payments/categories")
@RestController
public class PaymentCategoryController {
    private final PaymentCategoryService paymentCategoriesService;

    @GetMapping
    public ResponseEntity<List<PaymentCategoryDTO>> getAll() {
        log.info("Getting payments categories");

        List<PaymentCategoryDTO> categories = paymentCategoriesService.findAll().stream()
                .map(PaymentCategoryDTOMapper::apply)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("{id}")
    public ResponseEntity<PaymentCategoryDTO> getById(@PathVariable Long id) {
        log.info("Getting payment category by id - {}", id);
        PaymentCategory category = paymentCategoriesService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(PaymentCategoryDTOMapper.apply(category));
    }

    @PostMapping
    public ResponseEntity<PaymentCategoryDTO> create(@RequestBody PaymentCategoryCreationRequestDTO request) {
        log.info("Creating payment category: name - {}", request.getName());
        PaymentCategory category = paymentCategoriesService.create(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentCategoryDTOMapper.apply(category));
    }

    @PutMapping("{id}")
    public ResponseEntity<PaymentCategoryDTO> update(
            @PathVariable Long id,
            @RequestBody PaymentCategoryCreationRequestDTO request
    ) {
        log.info("Updating payment category: id - {} | name - {}", id, request.getName());
        PaymentCategory category = paymentCategoriesService.update(id, request.getName());
        return ResponseEntity.status(HttpStatus.OK).body(PaymentCategoryDTOMapper.apply(category));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting payment category: id - {}", id);
        paymentCategoriesService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
