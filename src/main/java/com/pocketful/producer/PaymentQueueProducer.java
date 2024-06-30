package com.pocketful.producer;

import com.pocketful.entity.Payment;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.pocketful.config.RabbitMqConfig.PAYMENT_PROCESS_QUEUE;

@AllArgsConstructor
@Service
public class PaymentQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    public void processPaymentGeneration(Payment payment) {
        rabbitTemplate.convertAndSend(PAYMENT_PROCESS_QUEUE, payment);
    }
}
