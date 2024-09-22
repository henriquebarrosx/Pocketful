package com.pocketful.controller;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.service.PaymentService;
import com.pocketful.util.SessionContext;
import com.pocketful.web.dto.payment.*;
import com.pocketful.web.mapper.PaymentDTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("v1/payments")
@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentDTOMapper paymentDTOMapper;

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAll(
        @RequestParam(required = false) LocalDate startAt,
        @RequestParam(required = false) LocalDate endAt) {

        Account account = SessionContext.get();
        log.info("Getting payments between two dates: account id - {} | started at - {} | ended at - {}", account.getId(), startAt, endAt);

        List<PaymentDTO> payments = paymentService.findBy(account, startAt, endAt).stream()
            .map(paymentDTOMapper)
            .toList();

        return ResponseEntity.status(HttpStatus.OK).body(payments);
    }

    @GetMapping("{id}")
    public ResponseEntity<PaymentDTO> get(@PathVariable Long id) {
        Account account = SessionContext.get();
        log.info("getting payment by id: account id - {} | payment id - {}", account.getId(), id);

        Payment payment = paymentService.findById(id, account);
        return ResponseEntity.status(HttpStatus.OK).body(paymentDTOMapper.apply(payment));
    }

    @PostMapping
    public ResponseEntity<PaymentIdDTO> create(
        @RequestBody @Validated PaymentCreationRequestDTO request) {

        Account account = SessionContext.get();
        log.info("Creating payment: account id - {} | description - {} | amount - {} | category id - {}", account.getId(), request.getDescription(), request.getAmount(), request.getPaymentCategoryId());
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
        log.info("Deleting payment by account: account id - {} | type - {}", account, request.getType());
        paymentService.delete(account, id, request.getType());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
