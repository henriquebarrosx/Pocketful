package com.pocketful.service;

import com.pocketful.entity.PaymentFrequency;
import com.pocketful.exception.BadRequestException;
import com.pocketful.exception.NotFoundException;
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
                .orElseThrow(() -> {
                    log.error("Payment frequency not found: id - {}", id);
                    return new NotFoundException("Payment frequency not found");
                });
    }

    public PaymentFrequency create(boolean isIndeterminate, int times) {
        log.info("Creating payment frequency: is indeterminate - {} | times - {}", isIndeterminate, times);

        int MIN_FREQUENCY_TIMES_IN_MONTHS = 1;
        int MAX_FREQUENCY_TIMES_IN_MONTHS = 600;

        if (isIndeterminate) {
            PaymentFrequency frequency = paymentFrequencyRepository.save(
                    PaymentFrequency.builder()
                            .times(MAX_FREQUENCY_TIMES_IN_MONTHS)
                            .build()
            );

            log.info("Payment frequency created successfully: is indeterminate - {} | times - {} | frequency id - {}", true, times, frequency.getId());
            return frequency;
        }

        if (times < MIN_FREQUENCY_TIMES_IN_MONTHS || times > MAX_FREQUENCY_TIMES_IN_MONTHS) {
            log.error("Failed creating payment frequency with invalid times: {}", times);
            throw new BadRequestException("Invalid frequency times");
        }

        PaymentFrequency frequency = paymentFrequencyRepository.save(getPaymentFrequencyBuilder(times));
        log.info("Payment frequency created successfully: is indeterminate - {} | times - {} | frequency id - {}", false, times, frequency.getId());
        return frequency;
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting payment frequency by id: {}", id);
        paymentFrequencyRepository.deleteById(id);
        log.info("Payment frequency deleted successfully by id: {}", id);
    }

    private static PaymentFrequency getPaymentFrequencyBuilder(int times) {
        return PaymentFrequency.builder()
                .times(times)
                .build();
    }
}
