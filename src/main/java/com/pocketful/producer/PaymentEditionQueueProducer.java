package com.pocketful.producer;

import com.pocketful.entity.Payment;
import com.pocketful.enums.PaymentSelectionOption;
import com.pocketful.web.dto.payment.PaymentEditionQueuePayload;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.pocketful.config.RabbitMqConfig.PAYMENTS_EDITION_QUEUE;

@AllArgsConstructor
@Service
public class PaymentEditionQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    public void processPaymentUpdate(Payment payment, PaymentSelectionOption type) {
        rabbitTemplate.convertAndSend(PAYMENTS_EDITION_QUEUE, new PaymentEditionQueuePayload(payment, type));
    }
}
