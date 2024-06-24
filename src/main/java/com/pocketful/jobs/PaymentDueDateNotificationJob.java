package com.pocketful.jobs;

import com.pocketful.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Slf4j
@AllArgsConstructor
@EnableScheduling
@Configuration
public class PaymentDueDateNotificationJob {
    private final PaymentService paymentService;

    @Scheduled(cron = "0 0 8 * * *")
    public void notifyInDeadlineDay() {
        paymentService.notifyPendingPaymentsByDate(LocalDate.now());
    }
}
