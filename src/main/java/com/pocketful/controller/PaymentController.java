package com.pocketful.controller;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.service.PaymentService;
import com.pocketful.util.SessionContext;
import com.pocketful.web.dto.payment.PaymentCreationRequestDTO;
import com.pocketful.web.dto.payment.PaymentDTO;
import com.pocketful.web.dto.payment.PaymentEditionRequestDTO;
import com.pocketful.web.dto.payment.PaymentIdDTO;
import com.pocketful.web.mapper.PaymentDTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startAt,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endAt) {

        Account account = SessionContext.get();
        log.info("Getting payments between two dates: account id - {} | started at - {} | ended at - {}", account.getId(), startAt, endAt);

        List<PaymentDTO> payments = paymentService.findBy(account, startAt, endAt).stream()
                .map(paymentDTOMapper)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(payments);
    }

    @PostMapping
    public ResponseEntity<PaymentIdDTO> create(
        @RequestBody PaymentCreationRequestDTO request) {

        Account account = SessionContext.get();
        log.info("Creating payment: account id - {} | description - {} | amount - {} | category id - {}", account.getId(), request.getDescription(), request.getAmount(), request.getPaymentCategoryId());
        Payment payment = paymentService.create(account, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PaymentIdDTO(payment.getId()));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody PaymentEditionRequestDTO request) {

        Account account = SessionContext.get();
        log.info("Updating payment: account id - {} | payment id - {} | type - {}", account.getId(), id, request.getType());
        paymentService.update(account, id, request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @RequestParam PaymentSelectionOption type) {

        Account account = SessionContext.get();
        log.info("Deleting payment by account: account id - {} | type - {}", account, type);
        paymentService.delete(account, id, type);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
