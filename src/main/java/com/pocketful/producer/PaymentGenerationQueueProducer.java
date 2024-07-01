package com.pocketful.producer;

import com.pocketful.entity.Payment;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.pocketful.config.RabbitMqConfig.PAYMENTS_GENERATION_QUEUE;

@AllArgsConstructor
@Service
public class PaymentGenerationQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    public void processPaymentGeneration(Payment payment) {
        rabbitTemplate.convertAndSend(PAYMENTS_GENERATION_QUEUE, payment);
    }
}
