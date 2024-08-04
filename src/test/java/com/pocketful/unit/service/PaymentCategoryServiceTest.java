package com.pocketful.unit.service;

import com.pocketful.entity.PaymentCategory;
import com.pocketful.exception.PaymentCategory.PaymentCategoryAlreadyExistException;
import com.pocketful.exception.PaymentCategory.PaymentCategoryNotFoundException;
import com.pocketful.repository.PaymentCategoryRepository;
import com.pocketful.service.PaymentCategoryService;
import com.pocketful.utils.PaymentCategoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PaymentCategoryServiceTest {

    @InjectMocks
    private PaymentCategoryService service;

    @Mock
    private PaymentCategoryRepository repository;

    @Test
    void shouldReturnAllWhenGettingAllCategories() {
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());
        List<PaymentCategory> categories = service.findAll();
        Assertions.assertEquals(0, categories.size());
    }

    @Test
    void shouldThrowExceptionWhenCreatingUsingNameThatAlreadyExist() {
        String name = "Transport";
        Mockito.when(repository.existsPaymentCategoryByName(name)).thenReturn(true);

        Exception exception = Assertions.assertThrows(PaymentCategoryAlreadyExistException.class, () -> service.create(name));

        Assertions.assertEquals(String.format("Payment category %s already exists.", name), exception.getMessage());
    }

    @Test
    void shouldReturnCreatedCategoryWhenCreatingUsingNameThatDoesNotExist() {
        String name = "Transport";
        PaymentCategory category = PaymentCategoryBuilder.build();

        Mockito.when(repository.existsPaymentCategoryByName(name)).thenReturn(false);
        Mockito.when(repository.save(ArgumentMatchers.any(PaymentCategory.class))).thenReturn(category);

        PaymentCategory createdCategory = service.create(name);

        Assertions.assertEquals(category.getId(), createdCategory.getId());
        Assertions.assertEquals(category.getName(), createdCategory.getName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingCategoryThatDoesNotExist() {
        String name = "Transport";
        PaymentCategory category = PaymentCategoryBuilder.build();

        Mockito.when(repository.findById(category.getId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(PaymentCategoryNotFoundException.class,
                () -> service.update(category.getId(), name));

        Assertions.assertEquals(String.format("Payment Category by id %s not found", category.getId()), exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUsingNameThatAlreadyExist() {
        String name = "Transport";
        PaymentCategory category = PaymentCategoryBuilder.build();

        Mockito.when(repository.findById(category.getId())).thenReturn(Optional.of(category));
        Mockito.when(repository.existsPaymentCategoryByName(name)).thenReturn(true);

        Exception exception = Assertions.assertThrows(PaymentCategoryAlreadyExistException.class,
                () -> service.update(category.getId(), name));

        Assertions.assertEquals(String.format("Payment category %s already exists.", name), exception.getMessage());
    }

    @Test
    void shouldReturnUpdatedCategoryWhenUpdatingUsingNameThatDoesNotExist() {
        String name = "Transport";
        PaymentCategory category = PaymentCategoryBuilder.build();

        Mockito.when(repository.findById(category.getId())).thenReturn(Optional.of(category));
        Mockito.when(repository.existsPaymentCategoryByName(name)).thenReturn(false);
        Mockito.when(repository.save(ArgumentMatchers.any(PaymentCategory.class))).thenReturn(category);

        PaymentCategory createdCategory = service.update(category.getId(), name);

        Assertions.assertEquals(category.getId(), createdCategory.getId());
        Assertions.assertEquals(category.getName(), createdCategory.getName());
    }

    @Test
    void shouldThrowExceptionWhenDeletingCategoryThatDoesNotExist() {
        PaymentCategory category = PaymentCategoryBuilder.build();

        Mockito.when(repository.findById(category.getId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(PaymentCategoryNotFoundException.class,
                () -> service.delete(category.getId()));

        Assertions.assertEquals(String.format("Payment Category by id %s not found", category.getId()), exception.getMessage());
    }

    @Test
    void shouldDeleteCategoryWhenItExist() {
        PaymentCategory category = PaymentCategoryBuilder.build();
        Mockito.when(repository.findById(category.getId())).thenReturn(Optional.of(category));

        service.delete(category.getId());

        Mockito.verify(repository, Mockito.times(1)).deleteById(category.getId());
    }

    @Test
    void shouldThrowExceptionWhenGettingCategoryThatDoesNotExist() {
        PaymentCategory category = PaymentCategoryBuilder.build();

        Mockito.when(repository.findById(category.getId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(PaymentCategoryNotFoundException.class,
                () -> service.findById(category.getId()));

        Assertions.assertEquals(String.format("Payment Category by id %s not found", category.getId()), exception.getMessage());
    }

    @Test
    void shouldReturnCategoryWhenGettingCategoryThatExist() {
        PaymentCategory category = PaymentCategoryBuilder.build();

        Mockito.when(repository.findById(category.getId())).thenReturn(Optional.of(category));

        PaymentCategory result = service.findById(category.getId());

        Assertions.assertEquals(category.getId(), result.getId());
        Assertions.assertEquals(category.getName(), result.getName());
    }
}
