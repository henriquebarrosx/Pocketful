package com.pocketful.service;

import com.pocketful.entity.PaymentFrequency;
import com.pocketful.exception.PaymentFrequency.InvalidFrequencyTimesException;
import com.pocketful.exception.PaymentFrequency.PaymentFrequencyNotFoundException;
import com.pocketful.repository.PaymentFrequencyRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentFrequencyService {
    private final PaymentFrequencyRepository paymentFrequencyRepository;

    public PaymentFrequency findById(Long id) {
        return paymentFrequencyRepository.findById(id)
                .orElseThrow(() -> new PaymentFrequencyNotFoundException(id));
    }

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
            throw new InvalidFrequencyTimesException();
        }

        return paymentFrequencyRepository.save(getPaymentFrequencyBuilder(times));
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting payment frequency by id: {}", id);
        PaymentFrequency frequency = findById(id);
        paymentFrequencyRepository.deleteById(frequency.getId());
    }

    private static PaymentFrequency getPaymentFrequencyBuilder(int times) {
        return PaymentFrequency.builder()
                .times(times)
                .build();
    }
}
