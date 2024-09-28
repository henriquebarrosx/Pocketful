package com.pocketful.service;

import com.pocketful.util.CurrencyFormatter;
import com.pocketful.entity.*;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.exception.Payment.InvalidPaymentAmountException;
import com.pocketful.exception.Payment.PaymentNotFoundException;
import com.pocketful.messaging.producer.PaymentEditionQueueProducer;
import com.pocketful.messaging.producer.PaymentGenerationQueueProducer;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.model.dto.payment.PaymentCreationRequestDTO;
import com.pocketful.model.dto.payment.PaymentEditionRequestDTO;
import com.pocketful.model.dto.payment.PaymentGenerationPayloadDTO;
import com.pocketful.model.view.payment.PaymentModel;
import freemarker.template.Template;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
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
        LocalDate from = Objects.isNull(startAt) ? LocalDate.of(1970, 11, 19) : startAt;
        LocalDate to = Objects.isNull(endAt) ? LocalDate.of(2970, 11, 19) : endAt;
        return paymentRepository.findByDeadlineAtBetween(account, from, to);
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(PaymentNotFoundException::new);
    }

    public Payment findById(Long id, Account account) {
        Payment payment = this.findById(id);

        if (payment.getAccount().getId().equals(account.getId())) {
            return payment;
        }

        throw new PaymentNotFoundException();
    }

    public Payment create(Account account, PaymentCreationRequestDTO paymentParams) {
        PaymentCategory paymentCategory = paymentCategoryService
                .findById(paymentParams.getPaymentCategoryId());

        if (!isValidAmount(paymentParams.getAmount())) {
            throw new InvalidPaymentAmountException();
        }

        PaymentFrequency paymentFrequency = paymentFrequencyService
                .create(paymentParams.getIsIndeterminate(), paymentParams.getFrequencyTimes());

        Payment payload = this.getPaymentBuilder(account, paymentParams, paymentCategory, paymentFrequency);
        Payment payment = paymentRepository.save(payload);

        log.info("Payment created successfully: account - {} | payment id - {}", account.getId(), payment.getId());

        paymentGenerationQueueProducer.processPaymentGeneration(payment);
        return payment;
    }

    private Payment getPaymentBuilder(Account account, PaymentCreationRequestDTO paymentParams, PaymentCategory paymentCategory, PaymentFrequency paymentFrequency) {
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
            throw new PaymentNotFoundException();
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
            throw new PaymentNotFoundException();
        }

        switch (type) {
            case THIS_PAYMENT -> {
                paymentRepository.delete(payment);
            }
            case THIS_AND_FUTURE_PAYMENTS -> {
                var frequency = payment.getPaymentFrequency();
                var deadlineAt = payment.getDeadlineAt();
                paymentRepository.deleteByDeadlineAtBetweenPresentAndFuture(frequency.getId(), account.getId(), deadlineAt);
            }
            case ALL_PAYMENTS -> {
                paymentRepository.deleteAllByPaymentFrequency(payment.getPaymentFrequency());
            }
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
                .findPendingBetweenNowAndDate(date);

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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return PaymentModel.builder()
                .description(payment.getDescription())
                .deadlineAt(dateTimeFormatter.format(payment.getDeadlineAt()))
                .amount(CurrencyFormatter.apply(payment.getAmount()))
                .isOverdue(payment.getDeadlineAt().isBefore(date))
                .build();
    }

    private List<Payment> getPaymentsBySelectionType(Payment payment, PaymentSelectionOption type) {
        List<Payment> payments = new ArrayList<>();

        switch (type) {
            case THIS_PAYMENT -> {
                payments.add(payment);
            }
            case THIS_AND_FUTURE_PAYMENTS -> {
                List<Payment> paymentsBetween = paymentRepository.findByDeadlineAtGreaterThanOrEqual(
                        payment.getAccount().getId(),
                        payment.getDeadlineAt());

                payments.addAll(paymentsBetween);
            }
            case ALL_PAYMENTS -> {
                List<Payment> allPayments = paymentRepository.findAllByPaymentFrequency(payment.getPaymentFrequency());
                payments.addAll(allPayments);
            }
        }

        return payments;
    }
}
