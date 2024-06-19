package com.pocketful.jobs;

import com.pocketful.entity.Currency;
import com.pocketful.entity.Payment;
import com.pocketful.service.PaymentService;
import com.pocketful.service.SmsNotificationService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Profile("!test")
@AllArgsConstructor
@EnableScheduling
@Configuration
public class PaymentDueDateNotificationJob {
    private final PaymentService paymentService;
    private final SmsNotificationService smsNotificationService;

    @Scheduled(fixedRate = 1800000)
    public void notifyInDeadlineDay() {
        notifyPendingPaymentsByDate(LocalDate.now());
    }

    private void notifyPendingPaymentsByDate(LocalDate date) {
        boolean isToday = date.isEqual(LocalDate.now());
        List<Payment> payments = paymentService.findPendingPaymentsByDate(date);

        payments.forEach(payment -> {
            Currency currency = new Currency(payment.getAmount());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String message = isToday
                    ? "Olá, %s. %s no valor de %s está para vencer hoje %s."
                    : "Olá, %s. %s no valor de %s está para vencer dia %s.";

            String template = String.format(
                    message,
                    payment.getAccount().getName(),
                    payment.getDescription(),
                    currency.getValue(),
                    dateTimeFormatter.format(payment.getDeadlineAt())
            );

            smsNotificationService.send(template, payment.getAccount());
        });
    }
}
