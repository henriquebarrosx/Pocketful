package com.pocketful.unit.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.exception.Payment.InvalidPaymentAmountException;
import com.pocketful.exception.Payment.PaymentNotFoundException;
import com.pocketful.exception.PaymentCategory.PaymentCategoryNotFoundException;
import com.pocketful.exception.PaymentFrequency.InvalidFrequencyTimesException;
import com.pocketful.producer.PaymentEditionQueueProducer;
import com.pocketful.producer.PaymentGenerationQueueProducer;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.service.PaymentCategoryService;
import com.pocketful.service.PaymentFrequencyService;
import com.pocketful.service.PaymentService;
import com.pocketful.utils.*;
import com.pocketful.web.dto.payment.PaymentCreationRequestDTO;
import com.pocketful.web.dto.payment.PaymentEditionRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentCategoryService paymentCategoryService;

    @Mock
    private PaymentFrequencyService paymentFrequencyService;

    @Mock
    private PaymentGenerationQueueProducer paymentGenerationQueueProducer;

    @Mock
    private PaymentEditionQueueProducer paymentEditionQueueProducer;

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

    @Test
    void shouldThrowExceptionWhenCreatingPaymentWithCategoryThatDoesNotExist() {
        Account account = AccountBuilder.build();
        var request = PaymentCreationRequestDTO.builder().paymentCategoryId(1L).build();
        Mockito.doThrow(new PaymentCategoryNotFoundException(request.getPaymentCategoryId()))
                .when(paymentCategoryService).findById(request.getPaymentCategoryId());

        Exception exception = Assertions.assertThrows(PaymentCategoryNotFoundException.class,
                () -> paymentService.create(account, request));

        Assertions.assertEquals(String.format("Payment Category by id %s not found", request.getPaymentCategoryId()), exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentWithInvalidAmount() {
        Account account = AccountBuilder.build();
        PaymentCategory category = PaymentCategoryBuilder.build();
        var request = PaymentCreationRequestBuilder.buildWithAmount(BigDecimal.valueOf(-1));
        Mockito.when(paymentCategoryService.findById(request.getPaymentCategoryId())).thenReturn(category);

        Exception exception = Assertions.assertThrows(InvalidPaymentAmountException.class,
                () -> paymentService.create(account, request));

        Assertions.assertEquals("Amount should be greater or equals 0.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaymentWithFrequencyGreaterThan600() {
        Account account = AccountBuilder.build();
        PaymentCategory category = PaymentCategoryBuilder.build();
        var request = PaymentCreationRequestBuilder.buildWithFrequency(601);

        Mockito.when(paymentCategoryService.findById(request.getPaymentCategoryId()))
                .thenReturn(category);
        Mockito.doThrow(new InvalidFrequencyTimesException())
                .when(paymentFrequencyService).create(ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt());

        Exception exception = Assertions.assertThrows(InvalidFrequencyTimesException.class,
                () -> paymentService.create(account, request));

        Assertions.assertEquals("Invalid frequency times", exception.getMessage());
    }

    @Test
    void shouldReturnNewPaymentWhenCreatingNewOneAndEnqueuePaymentGeneration() {
        Account account = AccountBuilder.build();
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build(10);
        PaymentCreationRequestDTO request = PaymentCreationRequestBuilder.buildWithFrequency(frequency.getTimes());
        Payment payment = PaymentBuilder.build(account, request, category, frequency);

        Mockito.when(paymentCategoryService.findById(request.getPaymentCategoryId()))
                .thenReturn(category);
        Mockito.when(paymentFrequencyService.create(ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt()))
                .thenReturn(frequency);
        Mockito.when(paymentRepository.save(ArgumentMatchers.any(Payment.class)))
                .thenReturn(payment);
        Mockito.doNothing().when(paymentGenerationQueueProducer)
                .processPaymentGeneration(ArgumentMatchers.any(Payment.class));

        Payment result = paymentService.create(account, request);

        Mockito.verify(paymentRepository, Mockito.times(1)).save(ArgumentMatchers.any(Payment.class));
        Mockito.verify(paymentGenerationQueueProducer, Mockito.times(1)).processPaymentGeneration(ArgumentMatchers.any(Payment.class));
        Assertions.assertEquals(result.getId(), payment.getId());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentPayment() {
        Account account = AccountBuilder.build();
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build(10);
        PaymentEditionRequestDTO request = PaymentEditionRequestBuilder.build();
        Payment payment = PaymentBuilder.build(account, request, category, frequency);
        Mockito.when(paymentRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(PaymentNotFoundException.class,
                () -> paymentService.update(account, payment.getId(), request));

        Assertions.assertEquals(String.format("Payment by id %s not found", payment.getId()), exception.getMessage());
    }
}
