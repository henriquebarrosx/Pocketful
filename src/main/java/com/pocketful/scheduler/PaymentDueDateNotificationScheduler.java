package com.pocketful.scheduler;

import com.pocketful.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Configuration
public class PaymentDueDateNotificationScheduler {
    private final PaymentService paymentService;

    @Scheduled(cron = "${scheduler.pending_payment_notification}")
    public void notifyInDeadlineDay() {
        paymentService.notifyPendingPaymentsByDate(LocalDate.now());
    }
}
