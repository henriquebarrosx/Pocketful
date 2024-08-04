package com.pocketful.unit.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.exception.Payment.PaymentNotFoundException;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.service.PaymentService;
import com.pocketful.utils.AccountBuilder;
import com.pocketful.utils.PaymentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    void shouldReturnAllPaymentsByAccountWhenStartAndEndAtIsNotProvided() {
        Account account = AccountBuilder.build();
        Mockito.when(paymentService.findBy(account, null, null)).thenReturn(Collections.emptyList());

        List<Payment> payments = paymentService.findBy(account, null, null);

        Assertions.assertEquals(payments.size(), 0);
        Mockito.verify(paymentRepository, Mockito.times(1))
                .findAllByAccountAndDeadlineAtBetweenOrderByCreatedAtAsc(account, LocalDate.MIN, LocalDate.MAX);
    }

    @Test
    void shouldReturnAllPaymentsByAccountBetweenTwoDatesWhenStartAndEndAtBeenProvided() {
        Account account = AccountBuilder.build();
        LocalDate startAt = LocalDate.of(2024, 10, 1);
        LocalDate endAt = LocalDate.of(2024, 10, 31);
        Mockito.when(paymentService.findBy(account, startAt, endAt)).thenReturn(Collections.emptyList());

        List<Payment> payments = paymentService.findBy(account, startAt, endAt);

        Assertions.assertEquals(payments.size(), 0);
        Mockito.verify(paymentRepository, Mockito.times(1))
                .findAllByAccountAndDeadlineAtBetweenOrderByCreatedAtAsc(account, startAt, endAt);
    }

    @Test
    void shouldThrowExceptionWhenGetPaymentByIdThatDoesNotExist() {
        Long id = 1L;
        Mockito.when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(PaymentNotFoundException.class, () -> paymentService.findById(id));

        Assertions.assertEquals(String.format("Payment by id %s not found", id), exception.getMessage());
    }

    @Test
    void shouldReturnPaymentWhenGetPaymentByIdThatExist() {
        Payment payment = PaymentBuilder.build();
        Mockito.when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        Payment result = paymentService.findById(payment.getId());

        Assertions.assertEquals(payment.getId(), result.getId());
    }
}
