package com.pocketful.unit.service;

import com.pocketful.entity.PaymentFrequency;
import com.pocketful.exception.PaymentFrequency.InvalidFrequencyTimesException;
import com.pocketful.exception.PaymentFrequency.PaymentFrequencyNotFoundException;
import com.pocketful.repository.PaymentFrequencyRepository;
import com.pocketful.service.PaymentFrequencyService;
import com.pocketful.utils.PaymentFrequencyBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PaymentFrequencyServiceTest {

    @InjectMocks
    private PaymentFrequencyService service;

    @Mock
    private PaymentFrequencyRepository repository;

    @Test
    void shouldThrowExceptionWhenGettingFrequencyThatDoesNotExist() {
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        Mockito.when(repository.findById(frequency.getId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(PaymentFrequencyNotFoundException.class,
                () -> service.findById(frequency.getId()));

        Assertions.assertEquals("Payment Frequency not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTimesRateBeenLessThan1() {
        boolean isIndeterminate = false;
        int times = 0;

        Exception exception = Assertions.assertThrows(InvalidFrequencyTimesException.class,
                () -> service.create(isIndeterminate, times));

        Assertions.assertEquals("Invalid frequency times", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTimesRateBeenGreaterThan600() {
        boolean isIndeterminate = false;
        int times = 601;

        Exception exception = Assertions.assertThrows(InvalidFrequencyTimesException.class,
                () -> service.create(isIndeterminate, times));

        Assertions.assertEquals("Invalid frequency times", exception.getMessage());
    }

    @Test
    void shouldSaveFrequencyWithNTimesWhenTimesHasBeenBetween1And600AndNotIndeterminate() {
        PaymentFrequency output = PaymentFrequencyBuilder.build();
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(output);
        boolean isIndeterminate = false;
        int times = 3;

        service.create(isIndeterminate, times);

        ArgumentCaptor<PaymentFrequency> captor = ArgumentCaptor.forClass(PaymentFrequency.class);
        Mockito.verify(repository, Mockito.times(1)).save(captor.capture());
        Assertions.assertEquals(3, captor.getValue().getTimes());
    }

    @Test
    void shouldReturnFrequencyWith600TimesWhenHasBeenIndeterminate() {
        PaymentFrequency output = PaymentFrequencyBuilder.build(600);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(output);
        boolean isIndeterminate = true;
        int times = 0;

        service.create(isIndeterminate, times);

        ArgumentCaptor<PaymentFrequency> captor = ArgumentCaptor.forClass(PaymentFrequency.class);
        Mockito.verify(repository, Mockito.times(1)).save(captor.capture());
        Assertions.assertEquals(600, captor.getValue().getTimes());
    }

    @Test
    void shouldThrowExceptionWhenDeletingFrequencyThatDoesNotExist() {
        Long id = 1L;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(PaymentFrequencyNotFoundException.class,
                () -> service.deleteById(id));

        Assertions.assertEquals("Payment Frequency not found", exception.getMessage());
    }

    @Test
    void shouldDeleteFrequencyWhenDeletingFrequencyThatExist() {
        PaymentFrequency frequency = PaymentFrequencyBuilder.build();
        Mockito.when(repository.findById(frequency.getId())).thenReturn(Optional.of(frequency));
        Mockito.doNothing().when(repository).deleteById(frequency.getId());

        service.deleteById(frequency.getId());

        Mockito.verify(repository, Mockito.times(1)).deleteById(frequency.getId());
    }
}
