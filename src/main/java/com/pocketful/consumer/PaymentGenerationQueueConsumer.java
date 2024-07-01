package com.pocketful.consumer;

import com.pocketful.entity.Payment;
import com.pocketful.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.pocketful.config.RabbitMqConfig.PAYMENTS_GENERATION_QUEUE;

@AllArgsConstructor
@Component
public class PaymentGenerationQueueConsumer {
    private final PaymentService paymentService;

    @RabbitListener(queues = PAYMENTS_GENERATION_QUEUE)
    public void receive(@Payload Payment payment) {
        paymentService.processPaymentGeneration(payment);
    }
}
