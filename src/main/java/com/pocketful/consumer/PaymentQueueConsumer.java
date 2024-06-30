package com.pocketful.consumer;

import com.pocketful.entity.Payment;
import com.pocketful.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.pocketful.config.RabbitMqConfig.PAYMENT_PROCESS_QUEUE;

@AllArgsConstructor
@Component
public class PaymentQueueConsumer {
    private final PaymentService paymentService;

    @RabbitListener(queues = PAYMENT_PROCESS_QUEUE)
    public void receive(@Payload Payment payment) {
        paymentService.processPaymentGeneration(payment);
    }
}
