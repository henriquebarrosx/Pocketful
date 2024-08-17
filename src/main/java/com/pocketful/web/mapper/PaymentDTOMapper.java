package com.pocketful.web.mapper;

import com.pocketful.entity.Payment;
import com.pocketful.web.dto.payment.PaymentDTO;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class PaymentDTOMapper implements Function<Payment, PaymentDTO> {
    @Override
    public PaymentDTO apply(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .description(payment.getDescription())
                .payed(payment.getPayed())
                .amount(payment.getAmount())
                .isExpense(payment.getIsExpense())
                .deadlineAt(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(payment.getDeadlineAt()))
                .paymentCategory(PaymentCategoryDTOMapper.apply(payment.getPaymentCategory()))
                .frequencyTimes(payment.getPaymentFrequency().getTimes())
                .build();
    }
}
