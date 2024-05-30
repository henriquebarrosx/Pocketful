package com.pocketful.controller;

import com.pocketful.service.PaymentService;
import com.pocketful.web.dto.payment.PaymentDTO;
import com.pocketful.web.mapper.PaymentDTOMapper;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RequestMapping("v1/payments")
@RestController
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentDTOMapper paymentDTOMapper;

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAll() {
        List<PaymentDTO> payments = paymentService.findAll().stream()
                .map(paymentDTOMapper)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(payments);

    }
}
