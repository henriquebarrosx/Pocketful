package com.pocketful.config;

import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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

    @Value("${rabbitmq.host}")
    private String QUEUE_SERVICE_HOSTNAME;

    @Value("${rabbitmq.port}")
    private String QUEUE_SERVICE_PORT;

    @Value("${rabbitmq.username}")
    private String QUEUE_SERVICE_USERNAME;

    @Value("${rabbitmq.password}")
    private String QUEUE_SERVICE_PASSWORD;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(QUEUE_SERVICE_HOSTNAME);
        connectionFactory.setUsername(QUEUE_SERVICE_USERNAME);
        connectionFactory.setPassword(QUEUE_SERVICE_PASSWORD);
        connectionFactory.setPort(Integer.parseInt(QUEUE_SERVICE_PORT));
        return connectionFactory;
    }

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
