package com.epam.resourceservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JmsConfig {

    @Value("${resource.processor.queue}")
    private String RESOURCE_PROCESSOR_QUEUE;

    @Value("${resource.processor.dl.queue}")
    private String RESOURCE_PROCESSOR_DL_QUEUE;

    @Bean
    Queue createDLQ() {
        return QueueBuilder
                .durable(RESOURCE_PROCESSOR_DL_QUEUE)
                .build();
    }

    @Bean
    Queue createProcessorQueue() {
        return QueueBuilder
                .durable(RESOURCE_PROCESSOR_QUEUE)
                .withArgument("x-dead-letter-exchange", "deadLetterExchange")
                .withArgument("x-dead-letter-routing-key", "deadLetter")
                .build();
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange("deadLetterExchange");
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange("resourceExchange");
    }

    @Bean
    Binding DLQBinding() {
        return BindingBuilder.bind(createDLQ()).to(deadLetterExchange()).with("deadLetter");
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(createProcessorQueue()).to(directExchange()).with("resourceProcessor");
    }

    @Bean
    @Primary
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter messageConvertor() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConvertor());
        return rabbitTemplate;
    }
}
