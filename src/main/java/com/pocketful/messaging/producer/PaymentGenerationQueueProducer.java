package com.pocketful.messaging.producer;

import com.pocketful.entity.Payment;
import com.pocketful.model.dto.payment.PaymentGenerationPayloadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGenerationQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.payments_generation_queue}")
    private String PAYMENTS_GENERATION_QUEUE;

    public void processPaymentGeneration(Payment payment) {
        log.info("Payment generation queue notified: payment id - {}", payment.getId());
        PaymentGenerationPayloadDTO payload = getPaymentGenerationPayloadDTOBuilder(payment);
        rabbitTemplate.convertAndSend(PAYMENTS_GENERATION_QUEUE, payload);
    }

    private PaymentGenerationPayloadDTO getPaymentGenerationPayloadDTOBuilder(Payment payment) {
        return PaymentGenerationPayloadDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .payed(payment.getPayed())
                .isExpense(payment.getIsExpense())
                .deadlineAt(payment.getDeadlineAt())
                .accountId(payment.getAccount().getId())
                .paymentCategoryId(payment.getPaymentCategory().getId())
                .paymentFrequencyId(payment.getPaymentFrequency().getId())
                .build();
    }
}
