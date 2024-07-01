package com.pocketful.service;

import com.pocketful.entity.Currency;
import com.pocketful.entity.*;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.exception.BadRequestException;
import com.pocketful.exception.NotFoundException;
import com.pocketful.producer.PaymentEditionQueueProducer;
import com.pocketful.producer.PaymentGenerationQueueProducer;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.web.dto.payment.NewPaymentDTO;
import com.pocketful.web.dto.payment.PaymentEditionRequestDTO;
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
    private final PaymentEditionQueueProducer paymentEditionQueueProducer;
    private final PaymentGenerationQueueProducer paymentGenerationQueueProducer;


    public List<Payment> findBy(LocalDate startAt, LocalDate endAt) {
        LocalDate MIN_DATE = LocalDate.of(1970, 1, 1);
        LocalDate MAX_DATE = LocalDate.of(9999, 1, 1);

        return paymentRepository.findAllByDeadlineAtBetween(
                Objects.isNull(startAt) ? MIN_DATE : startAt,
                Objects.isNull(endAt) ? MAX_DATE : endAt
        );
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
    }

    @Transactional
    public Payment create(NewPaymentDTO newPaymentDTO) {
        Account account = accountService.findById(newPaymentDTO.getAccountId());

        PaymentCategory paymentCategory = paymentCategoryService
                .findById(newPaymentDTO.getPaymentCategoryId());

        if (!isValidAmount(newPaymentDTO.getAmount())) {
            throw new BadRequestException("Amount should be greater or equals 0.");
        }

        PaymentFrequency paymentFrequency = paymentFrequencyService
                .create(newPaymentDTO.getIsIndeterminate(), newPaymentDTO.getFrequencyTimes());

        Payment payment = Payment.builder()
                .account(account)
                .paymentCategory(paymentCategory)
                .paymentFrequency(paymentFrequency)
                .amount(newPaymentDTO.getAmount())
                .description(newPaymentDTO.getDescription())
                .isExpense(newPaymentDTO.getIsExpense())
                .payed(newPaymentDTO.getPayed())
                .deadlineAt(newPaymentDTO.getDeadlineAt())
                .build();

        paymentRepository.save(payment);
        paymentGenerationQueueProducer.processPaymentGeneration(payment);
        return payment;
    }

    @Transactional
    public void update(Long id, PaymentEditionRequestDTO paymentParams) {
        Payment payment = findById(id);

        PaymentCategory paymentCategory = paymentCategoryService
                .findById(paymentParams.getPaymentCategoryId());

        if (!isValidAmount(paymentParams.getAmount())) {
            throw new BadRequestException("Amount should be greater or equals 0.");
        }

        payment.setAmount(paymentParams.getAmount());
        payment.setPaymentCategory(paymentCategory);
        payment.setDescription(paymentParams.getDescription());
        payment.setPayed(paymentParams.getPayed());
        payment.setIsExpense(paymentParams.getIsExpense());
        payment.setDeadlineAt(paymentParams.getDeadlineAt());

        paymentRepository.save(payment);
        paymentEditionQueueProducer.processPaymentUpdate(payment, paymentParams.getType());
    }

    public void processPaymentGeneration(Payment payment) {
        int batchSize = payment.getPaymentFrequency().getTimes();
        List<Payment> payments = new ArrayList<>(batchSize - 1);

        for (int index = 1; index < batchSize; index++) {
            LocalDate deadline = LocalDate.from(payment.getDeadlineAt())
                    .plusMonths(index);

            payments.add(
                    Payment.builder()
                            .amount(payment.getAmount())
                            .description(payment.getDescription())
                            .deadlineAt(deadline)
                            .payed(payment.getPayed())
                            .isExpense(payment.getIsExpense())
                            .paymentFrequency(payment.getPaymentFrequency())
                            .account(payment.getAccount())
                            .paymentCategory(payment.getPaymentCategory())
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
                    return data;
                }).toList();

        paymentRepository.saveAll(updatedPayments);
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

    @Transactional
    public void delete(Long id, PaymentSelectionOption type) {
        Payment payment = findById(id);

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
}
