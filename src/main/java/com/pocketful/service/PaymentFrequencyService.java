package com.pocketful.service;

import com.pocketful.entity.PaymentFrequency;
import com.pocketful.repository.PaymentFrequencyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PaymentFrequencyService {
    private final PaymentFrequencyRepository paymentFrequencyRepository;
    public static int CENTURY_FREQUENCY_TIMES = 1200;

    public PaymentFrequency create(boolean isIndeterminate, int times) {
        int frequencyTimes = isIndeterminate
                ? PaymentFrequencyService.CENTURY_FREQUENCY_TIMES
                : times;

        return paymentFrequencyRepository.save(
                PaymentFrequency.builder()
                        .times(frequencyTimes)
                        .build()
        );
    }
}
