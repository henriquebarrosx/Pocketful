package com.pocketful.service;

import com.pocketful.entity.PaymentFrequency;
import com.pocketful.exception.BadRequestException;
import com.pocketful.repository.PaymentFrequencyRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PaymentFrequencyService {
    private final PaymentFrequencyRepository paymentFrequencyRepository;

    public PaymentFrequency create(boolean isIndeterminate, int times) {
        int MIN_FREQUENCY_TIMES_IN_MONTHS = 1;
        int MAX_FREQUENCY_TIMES_IN_MONTHS = 600;

        if (isIndeterminate) {
            return paymentFrequencyRepository.save(
                PaymentFrequency.builder()
                    .times(MAX_FREQUENCY_TIMES_IN_MONTHS)
                    .build()
            );
        }

        if (times < MIN_FREQUENCY_TIMES_IN_MONTHS || times > MAX_FREQUENCY_TIMES_IN_MONTHS) {
            throw new BadRequestException("Invalid frequency times");
        }

        return paymentFrequencyRepository.save(
                PaymentFrequency.builder()
                        .times(times)
                        .build()
        );
    }

    @Transactional
    public void deleteById(Long id) {
        paymentFrequencyRepository.deleteById(id);
    }
}
