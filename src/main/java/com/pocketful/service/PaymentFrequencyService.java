package com.pocketful.service;

import com.pocketful.entity.PaymentFrequency;
import com.pocketful.exception.BadRequestException;
import com.pocketful.repository.PaymentFrequencyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PaymentFrequencyService {
    public static int CENTURY_FREQUENCY_TIMES = 1200;
    public static int INDETERMINATE_FREQUENCY_TIMES = 0;

    private final PaymentFrequencyRepository paymentFrequencyRepository;

    public PaymentFrequency create(boolean isIndeterminate, int times) {
        if (times < INDETERMINATE_FREQUENCY_TIMES || times > CENTURY_FREQUENCY_TIMES) {
            throw new BadRequestException("Frequency must to be between 0 and 1200");
        }

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
