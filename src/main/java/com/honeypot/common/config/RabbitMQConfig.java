package com.honeypot.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final String exchangeName;

    private final String commentQueue;

    private final String reactionQueue;

    private final String commentRoutingKey;

    private final String reactionRoutingKey;

    private final ObjectMapper objectMapper;

    public RabbitMQConfig(@Value("${notification.exchange-name}") String exchangeName,
                          @Value("${notification.queue-name.comment}") String commentQueue,
                          @Value("${notification.queue-name.reaction}") String reactionQueue,
                          @Value("${notification.routing-key.comment}") String commentRoutingKey,
                          @Value("${notification.routing-key.reaction}") String reactionRoutingKey,
                          ObjectMapper objectMapper) {
        this.exchangeName = exchangeName;
        this.commentQueue = commentQueue;
        this.reactionQueue = reactionQueue;
        this.commentRoutingKey = commentRoutingKey;
        this.reactionRoutingKey = reactionRoutingKey;
        this.objectMapper = objectMapper;
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    Queue commentQueue() {
        return new Queue(commentQueue);
    }

    @Bean
    Queue reactionQueue() {
        return new Queue(reactionQueue);
    }

    @Bean
    Binding bindingA(Queue commentQueue, TopicExchange exchange) {
        return BindingBuilder.bind(commentQueue).to(exchange).with(commentRoutingKey);
    }

    @Bean
    Binding bindingB(Queue reactionQueue, TopicExchange exchange) {
        return BindingBuilder.bind(reactionQueue).to(exchange).with(reactionRoutingKey);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
        return rabbitTemplate;
    }

}