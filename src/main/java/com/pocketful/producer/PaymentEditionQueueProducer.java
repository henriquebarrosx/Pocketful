package com.pocketful.producer;

import com.pocketful.entity.Payment;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.web.dto.payment.PaymentEditionQueuePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentEditionQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.payments_edition_queue}")
    private String PAYMENTS_EDITION_QUEUE;

    public void processPaymentUpdate(Payment payment, PaymentSelectionOption type) {
        rabbitTemplate.convertAndSend(PAYMENTS_EDITION_QUEUE, new PaymentEditionQueuePayload(payment, type));
    }
}
