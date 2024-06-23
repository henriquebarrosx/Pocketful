package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.exception.BadRequestException;
import com.pocketful.exception.InternalServerErrorException;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.web.dto.payment.NewPaymentDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentCategoryService paymentCategoryService;
    private final AccountService accountService;
    private final PaymentFrequencyService paymentFrequencyService;


    public List<Payment> findAll() {
        return paymentRepository.findAll();
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

    public List<Payment> findPendingPaymentsByDate(LocalDate date) {
        return paymentRepository
                .findAllByDeadlineAtLessThanEqualAndPayedIsFalseAndIsExpenseIsTrue(date);
    }
}
