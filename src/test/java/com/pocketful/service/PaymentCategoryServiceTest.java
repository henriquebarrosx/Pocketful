package com.pocketful.service;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.exception.ConflictException;
import com.pocketful.exception.NotFoundException;
import com.pocketful.repository.PaymentCategoryRepository;
import com.pocketful.web.dto.payment_category.NewPaymentCategoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentCategoryServiceTest {
    private AutoCloseable autoCloseable;

    @Mock
    private PaymentCategoryRepository paymentCategoryRepository;

    @InjectMocks
    private PaymentCategoryService paymentCategoryService;

    @BeforeEach
    public void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void shouldGetAllPaymentCategories() {
        List<PaymentCategory> categories = List.of(
            PaymentCategory.builder()
                .id(1L)
                .name("Saúde")
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build()
        );

        when(paymentCategoryRepository.findAll())
            .thenReturn(categories);

        assert(paymentCategoryService.findAll())
            .equals(categories);
    }

    @Test
    public void shouldThrowExceptionWhenCreateAlreadyRegisteredCategory() {
        when(paymentCategoryRepository.existsPaymentCategoryByName(any(String.class)))
            .thenReturn(true);

        Exception exception = assertThrows(
            ConflictException.class,
            () -> paymentCategoryService.create(new NewPaymentCategoryDTO("Saúde"))
        );

        assert(exception.getMessage()).equals("Payment category already exists.");
    }

    @Test
    public void shouldCreateSuccessfullyWhenCreateNonExistentCategoryName() {
        PaymentCategory paymentCategory = PaymentCategory.builder()
            .id(1L)
            .name("Saúde")
            .createdAt(LocalDate.now())
            .updatedAt(LocalDate.now())
            .build();

        when(paymentCategoryRepository.existsPaymentCategoryByName(any(String.class)))
            .thenReturn(false);

        when(paymentCategoryRepository.save(any(PaymentCategory.class)))
            .thenReturn(paymentCategory);

        assert(paymentCategoryService.create(new NewPaymentCategoryDTO("Saúde")))
            .equals(paymentCategory);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateNonExistentCategory() {
        when(paymentCategoryRepository.findById(any(Long.class)))
            .thenReturn(Optional.empty());

        Exception exception = assertThrows(
            NotFoundException.class,
            () -> paymentCategoryService.update(1L, new NewPaymentCategoryDTO("Cartão de crédito"))
        );

        assert(exception.getMessage()).equals("Payment Category not found");
    }

    @Test
    public void shouldThrowExceptionWhenUpdateAlreadyExistingCategory() {
        PaymentCategory paymentCategory = PaymentCategory.builder()
            .id(1L)
            .name("Saúde")
            .createdAt(LocalDate.now())
            .updatedAt(LocalDate.now())
            .build();

        when(paymentCategoryRepository.findById(any(Long.class)))
            .thenReturn(Optional.ofNullable(paymentCategory));

        when(paymentCategoryRepository.existsPaymentCategoryByName(any(String.class)))
            .thenReturn(true);

        ConflictException exception = assertThrows(
            ConflictException.class,
            () -> paymentCategoryService.update(
                paymentCategory.getId(),
                new NewPaymentCategoryDTO(paymentCategory.getName())
            )
        );

        assert(exception.getMessage()).equals("Payment category already exists.");
    }

    @Test
    public void shouldCreateSuccessfullyWhenUpdateExistentCategoryAndNameNotRegistered() {
        PaymentCategory paymentCategory = PaymentCategory.builder()
            .id(1L)
            .name("Saúde")
            .createdAt(LocalDate.now())
            .updatedAt(LocalDate.now())
            .build();

        when(paymentCategoryRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(paymentCategory));

        when(paymentCategoryRepository.existsPaymentCategoryByName(any(String.class)))
            .thenReturn(false);

        when(paymentCategoryRepository.save(any(PaymentCategory.class)))
            .thenReturn(paymentCategory);

        assert(
            paymentCategoryService.update(
                paymentCategory.getId(),
                new NewPaymentCategoryDTO(paymentCategory.getName())
            )
        ).equals(paymentCategory);
    }

    @Test
    public void shouldThrowExceptionWhenDeleteDontExistentCategoryById() {
        when(paymentCategoryRepository.findById(any(Long.class)))
            .thenReturn(Optional.empty());

        Exception exception = assertThrows(
            NotFoundException.class,
            () -> paymentCategoryService.delete(1L)
        );

        assert(exception.getMessage()).equals("Payment Category not found");
    }

    @Test
    public void shouldDeleteSuccessfullyWhenFoundCategoryById() {
        PaymentCategory paymentCategory = PaymentCategory.builder()
            .id(1L)
            .name("Saúde")
            .createdAt(LocalDate.now())
            .updatedAt(LocalDate.now())
            .build();

        when(paymentCategoryRepository.findById(paymentCategory.getId()))
            .thenReturn(Optional.of(paymentCategory));

        paymentCategoryService.delete(paymentCategory.getId());

        verify(paymentCategoryRepository).findById(paymentCategory.getId());
        verify(paymentCategoryRepository).deleteById(paymentCategory.getId());
    }
}