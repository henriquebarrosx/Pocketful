package com.pocketful.service;

import com.pocketful.entity.Currency;
import com.pocketful.entity.*;
import com.pocketful.exception.BadRequestException;
import com.pocketful.exception.InternalServerErrorException;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.web.dto.payment.NewPaymentDTO;
import com.pocketful.web.view.payment.PaymentModel;
import freemarker.template.Template;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PaymentService {
    private final EmailService emailService;
    private final AccountService accountService;
    private final PaymentRepository paymentRepository;
    private final PaymentCategoryService paymentCategoryService;
    private final PaymentFrequencyService paymentFrequencyService;


    public List<Payment> findBy(LocalDate startAt, LocalDate endAt) {
        LocalDate MIN_DATE = LocalDate.of(1970, 1, 1);
        LocalDate MAX_DATE = LocalDate.of(9999, 1, 1);

        return paymentRepository.findAllByDeadlineAtBetween(
            Objects.isNull(startAt) ? MIN_DATE : startAt,
            Objects.isNull(endAt) ? MAX_DATE : endAt
        );
    }

    @Transactional
    public Payment create(NewPaymentDTO newPaymentDTO) {
        Account account = accountService.findById(newPaymentDTO.getAccountId());

        PaymentCategory paymentCategory = paymentCategoryService
                .findById(newPaymentDTO.getPaymentCategoryId());

        if (!isValidAmount(newPaymentDTO.getAmount())) {
            throw new BadRequestException("Amount should be greater than 0.");
        }

        PaymentFrequency paymentFrequency = paymentFrequencyService
                .create(newPaymentDTO.getIsIndeterminate(), newPaymentDTO.getFrequencyTimes());

        List<Payment> payments = new ArrayList<>();

        for (int index = 0; index < paymentFrequency.getTimes(); index++) {
            LocalDate deadline = LocalDate.from(newPaymentDTO.getDeadlineAt())
                    .plusMonths(index);

            payments.add(
                    Payment.builder()
                            .amount(newPaymentDTO.getAmount())
                            .description(newPaymentDTO.getDescription())
                            .deadlineAt(deadline)
                            .payed(newPaymentDTO.getPayed())
                            .isExpense(newPaymentDTO.getIsExpense())
                            .paymentFrequency(paymentFrequency)
                            .account(account)
                            .paymentCategory(paymentCategory)
                            .build()
            );
        }

        paymentRepository.saveAll(payments);

        return payments.stream().findFirst()
                .orElseThrow(() -> new InternalServerErrorException("Something wrong when getting registered payment"));
    }

    private Boolean isValidAmount(float amount) {
        return amount > 0;
    }

    public void notifyPendingPaymentsByDate(LocalDate date) {
        List<Payment> payments = paymentRepository
                .findAllByDeadlineAtLessThanEqualAndPayedIsFalseAndIsExpenseIsTrue(date);

        Map<Account, List<Payment>> paymentsByAccounts = payments.stream()
                .collect(Collectors.groupingBy(Payment::getAccount));

        paymentsByAccounts.forEach((account, paymentList) -> {
            String to = account.getEmail();
            String subject = "Lembrete de Vencimento";
            Template template = emailService.getTemplate("email.ftl");

            List<PaymentModel> paymentsModel = paymentList.stream()
                    .map(payment -> convertPaymentToModel(payment, date))
                    .toList();

            Map<String, Object> model = new HashMap<>();
            model.put("account", account);
            model.put("payments", paymentsModel);

            emailService.send(to, subject, template, model);
        });
    }

    private PaymentModel convertPaymentToModel(Payment payment, LocalDate date) {
        Currency currency = new Currency(payment.getAmount());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return PaymentModel.builder()
                .description(payment.getDescription())
                .deadlineAt(dateTimeFormatter.format(payment.getDeadlineAt()))
                .amount(currency.getValue())
                .isOverdue(payment.getDeadlineAt().isBefore(date))
                .build();
    }
}
