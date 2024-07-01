package com.pocketful.config;

import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String PAYMENTS_GENERATION_QUEUE = "payments_generation_queue";
    public static final String PAYMENTS_EDITION_QUEUE = "payments_edition_queue";

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
