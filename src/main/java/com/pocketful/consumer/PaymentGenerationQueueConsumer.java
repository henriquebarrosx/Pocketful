package com.pocketful.consumer;

import com.pocketful.entity.Payment;
import com.pocketful.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.pocketful.config.RabbitMqConfig.PAYMENTS_GENERATION_QUEUE;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentGenerationQueueConsumer {
    private final PaymentService paymentService;

    @RabbitListener(queues = PAYMENTS_GENERATION_QUEUE)
    public void receive(@Payload Payment payment) {
        log.info("PaymentGenerationQueueConsumer.receive start payload: {}", payment);
        paymentService.processPaymentGeneration(payment);
        log.info("PaymentGenerationQueueConsumer.receive end payload: {}", payment);
    }
}
