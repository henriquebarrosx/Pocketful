package com.pocketful.messaging.consumer;

import com.pocketful.service.PaymentService;
import com.pocketful.model.dto.payment.PaymentGenerationPayloadDTO;
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
    public void receive(@Payload PaymentGenerationPayloadDTO payload) {
        log.info("Payment generation queue listener started: payment id - {}", payload.getId());
        paymentService.processPaymentGeneration(payload);
    }
}
