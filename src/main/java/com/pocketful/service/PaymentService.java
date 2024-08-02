package com.pocketful.service;

import com.pocketful.entity.Currency;
import com.pocketful.entity.*;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.exception.Payment.InvalidPaymentAmountException;
import com.pocketful.exception.Payment.PaymentNotFoundException;
import com.pocketful.producer.PaymentEditionQueueProducer;
import com.pocketful.producer.PaymentGenerationQueueProducer;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.web.dto.payment.NewPaymentDTO;
import com.pocketful.web.dto.payment.PaymentEditionRequestDTO;
import com.pocketful.web.dto.payment.PaymentGenerationPayloadDTO;
import com.pocketful.web.view.payment.PaymentModel;
import freemarker.template.Template;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentService {
    private final EmailService emailService;
    private final AccountService accountService;
    private final PaymentRepository paymentRepository;
    private final PaymentCategoryService paymentCategoryService;
    private final PaymentFrequencyService paymentFrequencyService;
    private final PaymentEditionQueueProducer paymentEditionQueueProducer;
    private final PaymentGenerationQueueProducer paymentGenerationQueueProducer;

    public List<Payment> findBy(Account account, LocalDate startAt, LocalDate endAt) {
        LocalDate MIN_DATE = LocalDate.of(1970, 1, 1);
        LocalDate MAX_DATE = LocalDate.of(9999, 1, 1);

        return paymentRepository.findAllByAccountAndDeadlineAtBetweenOrderByCreatedAtAsc(
            account,
            Objects.isNull(startAt) ? MIN_DATE : startAt,
            Objects.isNull(endAt) ? MAX_DATE : endAt
        );
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public Payment create(Account account, NewPaymentDTO paymentParams) {
        PaymentCategory paymentCategory = paymentCategoryService
            .findById(paymentParams.getPaymentCategoryId());

        if (!isValidAmount(paymentParams.getAmount())) {
            throw new InvalidPaymentAmountException();
        }

        PaymentFrequency paymentFrequency = paymentFrequencyService
            .create(paymentParams.getIsIndeterminate(), paymentParams.getFrequencyTimes());

        Payment payment = this.getPaymentBuilder(account, paymentParams, paymentCategory, paymentFrequency);

        paymentRepository.save(payment);
        log.info("Payment created successfully: account - {} | payment id - {}", account.getId(), payment.getId());

        paymentGenerationQueueProducer.processPaymentGeneration(payment);
        return payment;
    }

    private Payment getPaymentBuilder(Account account, NewPaymentDTO paymentParams, PaymentCategory paymentCategory, PaymentFrequency paymentFrequency) {
        return Payment.builder()
                .account(account)
                .paymentCategory(paymentCategory)
                .paymentFrequency(paymentFrequency)
                .amount(paymentParams.getAmount())
                .description(paymentParams.getDescription())
                .isExpense(paymentParams.getIsExpense())
                .payed(paymentParams.getPayed())
                .deadlineAt(paymentParams.getDeadlineAt())
                .build();
    }

    public void update(Account account, Long id, PaymentEditionRequestDTO paymentParams) {
        Payment payment = findById(id);

        if (Boolean.FALSE.equals(payment.getAccount().getId().equals(account.getId()))) {
            throw new PaymentNotFoundException(id);
        }

        PaymentCategory paymentCategory = paymentCategoryService
            .findById(paymentParams.getPaymentCategoryId());

        if (!isValidAmount(paymentParams.getAmount())) {
            throw new InvalidPaymentAmountException();
        }

        payment.setAmount(paymentParams.getAmount());
        payment.setPaymentCategory(paymentCategory);
        payment.setDescription(paymentParams.getDescription());
        payment.setPayed(paymentParams.getPayed());
        payment.setIsExpense(paymentParams.getIsExpense());
        payment.setDeadlineAt(paymentParams.getDeadlineAt());
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
        paymentEditionQueueProducer.processPaymentUpdate(payment, paymentParams.getType());
    }

    @Transactional
    public void delete(Account account, Long id, PaymentSelectionOption type) {
        Payment payment = findById(id);

        if (Boolean.FALSE.equals(payment.getAccount().getId().equals(account.getId()))) {
            throw new PaymentNotFoundException(id);
        }

        if (type.equals(PaymentSelectionOption.THIS_PAYMENT)) {
            paymentRepository.delete(payment);
        } else if (type.equals(PaymentSelectionOption.THIS_AND_FUTURE_PAYMENTS)) {
            paymentRepository.deleteAllByDeadlineAtGreaterThanEqual(payment.getDeadlineAt());
        } else if (type.equals(PaymentSelectionOption.ALL_PAYMENTS)) {
            paymentRepository.deleteAllByPaymentFrequency(payment.getPaymentFrequency());
        }

        boolean hasPayments = paymentRepository.existsPaymentByPaymentFrequency(payment.getPaymentFrequency());
        if (!hasPayments) paymentFrequencyService.deleteById(payment.getPaymentFrequency().getId());
    }

    public void processPaymentGeneration(PaymentGenerationPayloadDTO payload) {
        PaymentFrequency frequency = paymentFrequencyService.findById(payload.getPaymentFrequencyId());
        PaymentCategory category = paymentCategoryService.findById(payload.getPaymentCategoryId());
        Account account = accountService.findById(payload.getAccountId());

        List<Payment> payments = new ArrayList<>(frequency.getTimes() - 1);

        for (int index = 1; index < frequency.getTimes(); index++) {
            LocalDate deadline = LocalDate.from(payload.getDeadlineAt())
                .plusMonths(index);

            payments.add(
                Payment.builder()
                    .amount(payload.getAmount())
                    .description(payload.getDescription())
                    .payed(payload.getPayed())
                    .isExpense(payload.getIsExpense())
                    .deadlineAt(deadline)
                    .account(account)
                    .paymentCategory(category)
                    .paymentFrequency(frequency)
                    .build()
            );
        }

        paymentRepository.saveAll(payments);
    }

    public void processPaymentEdition(Payment payment, PaymentSelectionOption type) {
        List<Payment> updatedPayments = getPaymentsBySelectionType(payment, type)
            .stream()
            .map((data) -> {
                LocalDate deadline = data.getDeadlineAt().withDayOfMonth(payment.getDeadlineAt().getDayOfMonth());

                data.setAmount(payment.getAmount());
                data.setPaymentCategory(payment.getPaymentCategory());
                data.setDescription(payment.getDescription());
                data.setPayed(payment.getPayed());
                data.setIsExpense(payment.getIsExpense());
                data.setDeadlineAt(deadline);
                data.setUpdatedAt(LocalDateTime.now());
                return data;
            }).toList();

        paymentRepository.saveAll(updatedPayments);
    }

    public void notifyPendingPaymentsByDate(LocalDate date) {
        List<Payment> payments = paymentRepository
            .findAllByDeadlineAtLessThanEqualAndPayedIsFalseAndIsExpenseIsTrue(date);

        Map<Account, List<Payment>> paymentsByAccounts = payments.stream()
            .collect(Collectors.groupingBy(Payment::getAccount));

        paymentsByAccounts.forEach((account, paymentList) -> {
            String to = account.getEmail();
            String subject = "Lembrete de Vencimento";
            Template htmlTemplate = emailService.getTemplate("lembrete-vencimento-html.ftl");
            Template textTemplate = emailService.getTemplate("lembrete-vencimento-text.ftl");

            List<PaymentModel> paymentsModel = paymentList.stream()
                .map(payment -> convertPaymentToModel(payment, date))
                .toList();

            Map<String, Object> model = new HashMap<>();
            model.put("account", account);
            model.put("payments", paymentsModel);

            emailService.send(to, subject, textTemplate, htmlTemplate, model);
            log.info("Due date reminder sent to account email: account id - {} ({}) | total payments - {}", account.getId(), to, paymentsModel.size());
        });
    }

    private Boolean isValidAmount(BigDecimal amount) {
        return amount.compareTo(BigDecimal.valueOf(0)) >= 0;
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

    private List<Payment> getPaymentsBySelectionType(Payment payment, PaymentSelectionOption type) {
        List<Payment> payments = new ArrayList<>();

        if (type.equals(PaymentSelectionOption.THIS_PAYMENT)) {
            payments.add(payment);
        } else if (type.equals(PaymentSelectionOption.THIS_AND_FUTURE_PAYMENTS)) {
            List<Payment> paymentsBetween = paymentRepository.findAllByDeadlineAtGreaterThanEqual(payment.getDeadlineAt());
            payments.addAll(paymentsBetween);
        } else if (type.equals(PaymentSelectionOption.ALL_PAYMENTS)) {
            List<Payment> allPayments = paymentRepository.findAllByPaymentFrequency(payment.getPaymentFrequency());
            payments.addAll(allPayments);
        }

        return payments;
    }
}
