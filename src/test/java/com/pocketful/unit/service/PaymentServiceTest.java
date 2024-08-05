package com.pocketful.unit.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.enums.PaymentSelectionOption;
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
import org.mockito.*;
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

        Mockito.when(paymentCategoryService.findById(request.getPaymentCategoryId()))
                .thenReturn(category);

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
        Mockito.doThrow(new InvalidFrequencyTimesException()).when(paymentFrequencyService)
                .create(ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt());

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

        Mockito.verify(paymentRepository, Mockito.times(1))
                .save(ArgumentMatchers.any(Payment.class));
        Mockito.verify(paymentGenerationQueueProducer, Mockito.times(1))
                .processPaymentGeneration(ArgumentMatchers.any(Payment.class));
        Assertions.assertEquals(result.getId(), payment.getId());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentPayment() {
        Account account = AccountBuilder.build();
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        PaymentEditionRequestDTO request = PaymentEditionRequestBuilder.build();
        Payment payment = PaymentBuilder.build(account, request, category, frequency);
        Mockito.when(paymentRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(PaymentNotFoundException.class,
                () -> paymentService.update(account, payment.getId(), request));

        Assertions.assertEquals(String.format("Payment by id %s not found", payment.getId()), exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPaymentThatBelongsToAnotherAccount() {
        Account accountFromRequest = AccountBuilder.buildWithId(1L);
        Account accountFromPayment = AccountBuilder.buildWithId(2L);
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        PaymentEditionRequestDTO request = PaymentEditionRequestBuilder.build();
        Payment payment = PaymentBuilder.build(accountFromPayment, request, category, frequency);

        Mockito.when(paymentRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(payment));

        Exception exception = Assertions.assertThrows(PaymentNotFoundException.class,
                () -> paymentService.update(accountFromRequest, payment.getId(), request));

        Assertions.assertEquals(String.format("Payment by id %s not found", payment.getId()), exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPaymentWithCategoryThatDoesNotExist() {
        Account account = AccountBuilder.build();
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        PaymentEditionRequestDTO request = PaymentEditionRequestBuilder.buildWithCategory(category);
        Payment payment = PaymentBuilder.build(account, request, category, frequency);

        Mockito.when(paymentRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(payment));
        Mockito.doThrow(new PaymentCategoryNotFoundException(request.getPaymentCategoryId()))
                .when(paymentCategoryService).findById(request.getPaymentCategoryId());

        Exception exception = Assertions.assertThrows(PaymentCategoryNotFoundException.class,
                () -> paymentService.update(account, payment.getId(), request));

        Assertions.assertEquals(String.format("Payment Category by id %s not found", request.getPaymentCategoryId()), exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPaymentWithInvalidAmount() {
        Account account = AccountBuilder.build();
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        PaymentEditionRequestDTO request = PaymentEditionRequestBuilder.buildWithAmount(BigDecimal.valueOf(-1));
        Payment payment = PaymentBuilder.build(account, request, category, frequency);

        Mockito.when(paymentRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(payment));
        Mockito.when(paymentCategoryService.findById(request.getPaymentCategoryId()))
                .thenReturn(category);

        Exception exception = Assertions.assertThrows(InvalidPaymentAmountException.class,
                () -> paymentService.update(account, payment.getId(), request));

        Assertions.assertEquals("Amount should be greater or equals 0.", exception.getMessage());
    }

    @Test
    void shouldReturnNothingWhenUpdatingPaymentAndEnqueuePaymentEdition() {
        Account account = AccountBuilder.build();
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        PaymentEditionRequestDTO request = PaymentEditionRequestBuilder.build();
        Payment storedPayment = PaymentBuilder.build(account, request, category, frequency);

        var paymentArgCaptor = ArgumentCaptor.forClass(Payment.class);
        var typeArgCaptor = ArgumentCaptor.forClass(PaymentSelectionOption.class);
        Mockito.when(paymentRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(storedPayment));
        Mockito.when(paymentCategoryService.findById(request.getPaymentCategoryId())).thenReturn(category);
        Mockito.when(paymentRepository.save(ArgumentMatchers.any(Payment.class))).thenReturn(storedPayment);

        paymentService.update(account, storedPayment.getId(), request);

        Mockito.verify(paymentRepository, Mockito.times(1))
                .save(ArgumentMatchers.any(Payment.class));
        Mockito.verify(paymentEditionQueueProducer, Mockito.times(1))
                .processPaymentUpdate(paymentArgCaptor.capture(), typeArgCaptor.capture());

        Assertions.assertEquals(request.getPaymentCategoryId(), paymentArgCaptor.getValue().getPaymentCategory().getId());
        Assertions.assertEquals(request.getAmount(), paymentArgCaptor.getValue().getAmount());
        Assertions.assertEquals(request.getDescription(), paymentArgCaptor.getValue().getDescription());
        Assertions.assertEquals(request.getPayed(), paymentArgCaptor.getValue().getPayed());
        Assertions.assertEquals(request.getIsExpense(), paymentArgCaptor.getValue().getIsExpense());
        Assertions.assertEquals(request.getDeadlineAt(), paymentArgCaptor.getValue().getDeadlineAt());
        Assertions.assertEquals(request.getType().name(), typeArgCaptor.getValue().name());
    }

    @Test
    void shouldThrowExceptionWhenDeleteNonExistentPayment() {
        Long paymentId = 1L;
        Account account = AccountBuilder.build();
        Mockito.when(paymentRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(PaymentNotFoundException.class,
                () -> paymentService.delete(account, paymentId, PaymentSelectionOption.THIS_PAYMENT));

        Assertions.assertEquals(String.format("Payment by id %s not found", paymentId), exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingPaymentThatBelongsToAnotherAccount() {
        Long paymentId = 1L;
        Account accountFromRequest = AccountBuilder.buildWithId(1L);
        Account accountFromPayment = AccountBuilder.buildWithId(2L);
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        Payment payment = PaymentBuilder.build(accountFromPayment, category, frequency);

        Mockito.when(paymentRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(payment));

        Exception exception = Assertions.assertThrows(PaymentNotFoundException.class,
                () -> paymentService.delete(accountFromRequest, paymentId, PaymentSelectionOption.THIS_PAYMENT));

        Assertions.assertEquals(String.format("Payment by id %s not found", paymentId), exception.getMessage());
    }

    @Test
    void shouldDeleteOnlyTargetPaymentWhenSelectionTypeIsEqualsThisPayment() {
        Long paymentId = 1L;
        Account account = AccountBuilder.buildWithId(1L);
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        Payment payment = PaymentBuilder.build(account, category, frequency);

        var deleteArgCaptor = ArgumentCaptor.forClass(Payment.class);
        Mockito.when(paymentRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(payment));
        Mockito.doNothing().when(paymentRepository).delete(ArgumentMatchers.any(Payment.class));

        paymentService.delete(account, paymentId, PaymentSelectionOption.THIS_PAYMENT);

        Mockito.verify(paymentRepository, Mockito.times(1))
                .delete(deleteArgCaptor.capture());
        Mockito.verify(paymentRepository, Mockito.times(0))
                .deleteOnlyCurrentAndFuturePayment(
                        ArgumentMatchers.any(PaymentFrequency.class),
                        ArgumentMatchers.any(Account.class),
                        ArgumentMatchers.any(LocalDate.class)
                );
        Mockito.verify(paymentRepository, Mockito.times(0))
                .deleteAllByPaymentFrequency(ArgumentMatchers.any(PaymentFrequency.class));
        Assertions.assertEquals(paymentId, deleteArgCaptor.getValue().getId());
    }

    @Test
    void shouldDeleteCurrentAndFuturePaymentsByFrequencyThatBelongsToSignedAccountWhenSelectionTypeIsEqualsThisAndFuturePayment() {
        Long paymentId = 1L;
        Account account = AccountBuilder.buildWithId(1L);
        PaymentCategory category = PaymentCategoryBuilder.build();
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        Payment payment = PaymentBuilder.build(account, category, frequency);

        var targetFrequencyArgCaptor = ArgumentCaptor.forClass(PaymentFrequency.class);
        var targetAccountArgCaptor = ArgumentCaptor.forClass(Account.class);
        var targetDeadlineArgCaptor = ArgumentCaptor.forClass(LocalDate.class);
        Mockito.when(paymentRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(payment));

        paymentService.delete(account, paymentId, PaymentSelectionOption.THIS_AND_FUTURE_PAYMENTS);

        Mockito.verify(paymentRepository, Mockito.times(0))
                .delete(ArgumentMatchers.any(Payment.class));
        Mockito.verify(paymentRepository, Mockito.times(0))
                .deleteAllByPaymentFrequency(ArgumentMatchers.any(PaymentFrequency.class));

        Mockito.verify(paymentRepository, Mockito.times(1))
                .deleteOnlyCurrentAndFuturePayment(
                        targetFrequencyArgCaptor.capture(),
                        targetAccountArgCaptor.capture(),
                        targetDeadlineArgCaptor.capture());

        Assertions.assertEquals(frequency.getId(), targetFrequencyArgCaptor.getValue().getId());
        Assertions.assertEquals(account.getId(), targetAccountArgCaptor.getValue().getId());
        Assertions.assertEquals(payment.getDeadlineAt(), targetDeadlineArgCaptor.getValue());
    }
}
