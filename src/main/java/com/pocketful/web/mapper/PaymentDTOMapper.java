package com.pocketful.web.mapper;

import com.pocketful.entity.Payment;
import com.pocketful.web.dto.payment.PaymentDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@AllArgsConstructor
@Service
public class PaymentDTOMapper implements Function<Payment, PaymentDTO> {
    private final PaymentCategoryDTOMapper paymentCategoryDTOMapper;

    @Override
    public PaymentDTO apply(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .description(payment.getDescription())
                .payed(payment.getPayed())
                .amount(payment.getAmount())
                .isExpense(payment.getIsExpense())
                .deadlineAt(payment.getDeadlineAt())
                .paymentCategory(paymentCategoryDTOMapper.apply(payment.getPaymentCategory()))
                .frequencyTimes(payment.getPaymentFrequency().getTimes())
                .build();
    }
}
