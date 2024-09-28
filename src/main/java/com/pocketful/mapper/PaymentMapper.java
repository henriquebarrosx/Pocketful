package com.pocketful.mapper;

import com.pocketful.entity.Payment;
import com.pocketful.model.dto.payment.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class PaymentMapper {
    private final PaymentCategoryMapper paymentCategoryMapper;

    public PaymentDTO toDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .description(payment.getDescription())
                .payed(payment.getPayed())
                .amount(payment.getAmount())
                .isExpense(payment.getIsExpense())
                .deadlineAt(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(payment.getDeadlineAt()))
                .paymentCategory(paymentCategoryMapper.toDTO(payment.getPaymentCategory()))
                .frequencyTimes(payment.getPaymentFrequency().getTimes())
                .build();
    }
}
