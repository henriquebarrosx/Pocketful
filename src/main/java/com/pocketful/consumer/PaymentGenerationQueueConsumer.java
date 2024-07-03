package com.pocketful.consumer;

import com.pocketful.entity.Payment;
import com.pocketful.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentGenerationQueueConsumer {
    private final PaymentService paymentService;

    @RabbitListener(queues = {"${queue.payments_generation_queue}"})
    public void receive(@Payload Payment payment) {
        log.info("PaymentGenerationQueueConsumer.receive start payload: {}", payment);
        paymentService.processPaymentGeneration(payment);
        log.info("PaymentGenerationQueueConsumer.receive end payload: {}", payment);
    }
}
