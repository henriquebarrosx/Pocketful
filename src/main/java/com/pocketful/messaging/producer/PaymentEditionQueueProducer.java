package com.pocketful.messaging.producer;

import com.pocketful.entity.Payment;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.web.dto.payment.PaymentEditionQueuePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentEditionQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.payments_edition_queue}")
    private String PAYMENTS_EDITION_QUEUE;

    public void processPaymentUpdate(Payment payment, PaymentSelectionOption type) {
        log.info("Payment edition queue notified: payment id - {} | type {}", payment.getId(), type);
        rabbitTemplate.convertAndSend(PAYMENTS_EDITION_QUEUE, new PaymentEditionQueuePayload(payment, type));
    }
}
