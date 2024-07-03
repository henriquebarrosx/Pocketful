package com.pocketful.producer;

import com.pocketful.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentGenerationQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.payments_generation_queue}")
    private String PAYMENTS_GENERATION_QUEUE;

    public void processPaymentGeneration(Payment payment) {
        rabbitTemplate.convertAndSend(PAYMENTS_GENERATION_QUEUE, payment);
    }
}
