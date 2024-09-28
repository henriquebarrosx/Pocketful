package com.pocketful.controller;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.util.SessionContext;
import com.pocketful.service.PaymentService;
import com.pocketful.model.dto.payment.PaymentDTO;
import com.pocketful.mapper.PaymentMapper;
import com.pocketful.model.dto.payment.PaymentIdDTO;
import com.pocketful.model.dto.payment.PaymentEditionRequestDTO;
import com.pocketful.model.dto.payment.PaymentCreationRequestDTO;
import com.pocketful.model.dto.payment.PaymentDeletionRequestDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("v1/payments")
@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAll(
        @RequestParam(required = false) LocalDate startAt,
        @RequestParam(required = false) LocalDate endAt) {
        Account account = SessionContext.get();
        log.info("Getting payments between two dates: account id - {} | started at - {} | ended at - {}", account.getId(), startAt, endAt);

        List<PaymentDTO> payments = paymentService.findBy(account, startAt, endAt).stream()
            .map(paymentMapper::toDTO)
            .toList();

        return ResponseEntity.status(HttpStatus.OK).body(payments);
    }

    @GetMapping("{id}")
    public ResponseEntity<PaymentDTO> get(@PathVariable Long id) {
        Account account = SessionContext.get();
        log.info("Getting payment by id: account id - {} | payment id - {}", account.getId(), id);

        Payment payment = paymentService.findById(id, account);
        return ResponseEntity.status(HttpStatus.OK).body(paymentMapper.toDTO(payment));
    }

    @PostMapping
    public ResponseEntity<PaymentIdDTO> create(@RequestBody @Validated PaymentCreationRequestDTO request) {
        Account account = SessionContext.get();
        log.info("Creating payment: account id - {} | description - {} | amount - {}", account.getId(), request.getDescription(), request.getAmount());
        Payment payment = paymentService.create(account, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PaymentIdDTO(payment.getId()));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(
        @PathVariable Long id,
        @RequestBody @Validated PaymentEditionRequestDTO request) {
        Account account = SessionContext.get();
        log.info("Updating payment: account id - {} | payment id - {} | type - {}", account.getId(), id, request.getType());
        paymentService.update(account, id, request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @RequestBody @Validated PaymentDeletionRequestDTO request) {
        Account account = SessionContext.get();
        log.info("Deleting payment: account id - {} | id = {} | type - {}", account, id, request.getType());
        paymentService.delete(account, id, request.getType());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
