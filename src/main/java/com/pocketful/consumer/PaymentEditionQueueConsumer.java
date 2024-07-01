package com.pocketful.consumer;

import com.pocketful.service.PaymentService;
import com.pocketful.web.dto.payment.PaymentEditionQueuePayload;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.pocketful.config.RabbitMqConfig.PAYMENTS_EDITION_QUEUE;

@AllArgsConstructor
@Component
public class PaymentEditionQueueConsumer {
    private final PaymentService paymentService;

    @RabbitListener(queues = PAYMENTS_EDITION_QUEUE)
    public void receive(@Payload PaymentEditionQueuePayload payload) {
        paymentService.processPaymentEdition(payload.getPayment(), payload.getType());
    }
}
