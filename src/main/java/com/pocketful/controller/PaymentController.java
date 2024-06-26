package com.pocketful.controller;

import com.pocketful.entity.Payment;
import com.pocketful.service.PaymentService;
import com.pocketful.web.dto.payment.*;
import com.pocketful.web.mapper.PaymentDTOMapper;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RequestMapping("v1/payments")
@RestController
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentDTOMapper paymentDTOMapper;

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAll(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startAt,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endAt
    ) {
        List<PaymentDTO> payments = paymentService.findBy(startAt, endAt).stream()
                .map(paymentDTOMapper)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(payments);

    }

    @PostMapping
    public ResponseEntity<PaymentIdDTO> create(@RequestBody NewPaymentDTO newPaymentDTO) {
        Payment payment = paymentService.create(newPaymentDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new PaymentIdDTO(payment.getId()));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestBody PaymentDeleteDTO request) {
        paymentService.delete(id, request.type());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody PaymentEditionRequestDTO request) {
        paymentService.update(id, request);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
