package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.Payment;
import com.pocketful.entity.PaymentCategory;
import com.pocketful.entity.PaymentFrequency;
import com.pocketful.repository.PaymentRepository;
import com.pocketful.exception.BadRequestException;
import com.pocketful.web.dto.payment.NewPaymentDTO;
import com.pocketful.exception.InternalServerErrorException;

import lombok.AllArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

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
        if (!isValidAmount(newPaymentDTO.getAmount())) {
            throw new BadRequestException("Amount should be greater than 0.");
        }

        if (!isValidFrequencyTimes(newPaymentDTO.getFrequencyTimes())) {
            throw new BadRequestException("Frequency times should be greater than 0.");
        }

        PaymentFrequency paymentFrequency = paymentFrequencyService.create(
                newPaymentDTO.getIsIndeterminate(),
                newPaymentDTO.getFrequencyTimes()
        );

        PaymentCategory paymentCategory = paymentCategoryService
                .findById(newPaymentDTO.getPaymentCategoryId());

        Account account = accountService
                .findById(newPaymentDTO.getAccountId());

        List<Payment> payments = new ArrayList<>();

        for (int i = 0; i < paymentFrequency.getTimes(); i++) {
            LocalDate deadline = LocalDate.from(newPaymentDTO.getDeadlineAt())
                    .plusMonths(i);

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

    private Boolean isValidFrequencyTimes(float times) {
        return times >= 0;
    }
}
