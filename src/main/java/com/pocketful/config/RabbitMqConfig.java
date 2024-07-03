package com.pocketful.config;

import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Value("${queue.payments_generation_queue}")
    private String PAYMENTS_GENERATION_QUEUE;

    @Value("${queue.payments_edition_queue}")
    private String PAYMENTS_EDITION_QUEUE;

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Declarable processPaymentsGeneration() {
        return new Queue(PAYMENTS_GENERATION_QUEUE);
    }

    @Bean
    public Declarable processPaymentsEdition() {
        return new Queue(PAYMENTS_EDITION_QUEUE);
    }
}
