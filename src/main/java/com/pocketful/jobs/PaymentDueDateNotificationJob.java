package com.pocketful.jobs;

import com.pocketful.entity.Account;
import com.pocketful.entity.Currency;
import com.pocketful.entity.Payment;
import com.pocketful.service.EmailService;
import com.pocketful.service.PaymentService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import freemarker.template.Template;
import com.pocketful.web.view.payment.PaymentModel;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@EnableScheduling
@Configuration
public class PaymentDueDateNotificationJob {
    private final EmailService emailService;
    private final PaymentService paymentService;

    @Scheduled(cron = "0 0 8 * * *")
    public void notifyInDeadlineDay() {
        notifyPendingPaymentsByDate(LocalDate.now());
    }

    private void notifyPendingPaymentsByDate(LocalDate date) {
        List<Payment> payments = paymentService.findPendingPaymentsByDate(date);

        Map<Account, List<Payment>> paymentsByAccounts = payments.stream()
                .collect(Collectors.groupingBy(Payment::getAccount));

        paymentsByAccounts.forEach((account, paymentList) -> {
            String to = account.getEmail();
            String subject = "Lembrete de Vencimento";
            Template template = emailService.getTemplate("email.ftl");

            List<PaymentModel> paymentsModel = paymentList.stream()
                    .map(payment -> {
                        Currency currency = new Currency(payment.getAmount());
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                        return PaymentModel.builder()
                                .description(payment.getDescription())
                                .deadlineAt(dateTimeFormatter.format(payment.getDeadlineAt()))
                                .amount(currency.getValue())
                                .isOverdue(payment.getDeadlineAt().isBefore(date))
                                .build();
                    })
                    .toList();

            Map<String, Object> model = new HashMap<>();
            model.put("account", account);
            model.put("payments", paymentsModel);

            emailService.send(to, subject, template, model);
        });
    }
}
