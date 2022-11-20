package com.honeypot.domain.notification.service;

import com.honeypot.common.event.CommentCreatedEvent;
import com.honeypot.common.event.ReactionCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Primary
@Service
public class NotificationSendServiceMQImpl implements NotificationSendService {

    private final String exchangeName;

    private final String commentCreatedEventKey;

    private final String reactionCreatedEventKey;

    private final RabbitTemplate rabbitTemplate;

    public NotificationSendServiceMQImpl(
            @Value("${notification.exchange-name}") String exchangeName,
            @Value("${notification.routing-key.comment}") String commentCreatedEventKey,
            @Value("${notification.routing-key.reaction}") String reactionCreatedEventKey,
            RabbitTemplate rabbitTemplate
    ) {
        this.exchangeName = exchangeName;
        this.commentCreatedEventKey = commentCreatedEventKey;
        this.reactionCreatedEventKey = reactionCreatedEventKey;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(CommentCreatedEvent commentCreatedEvent) {
        rabbitTemplate.convertAndSend(exchangeName, commentCreatedEventKey, commentCreatedEvent);
    }

    @Override
    public void send(ReactionCreatedEvent reactionCreatedEvent) {
        rabbitTemplate.convertAndSend(exchangeName, reactionCreatedEventKey, reactionCreatedEvent);
    }

}