package com.honeypot.common.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventListener {

    private final String exchangeName;

    private final String commentCreatedEventKey;

    private final String reactionCreatedEventKey;

    private final RabbitTemplate rabbitTemplate;

    public ApplicationEventListener(
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

    @Async
    @EventListener
    public void listenCommentCreatedEvent(CommentCreatedEvent event) {
        rabbitTemplate.convertAndSend(exchangeName, commentCreatedEventKey, event);
    }

    @Async
    @EventListener
    public void listenReactionCreatedEvent(ReactionCreatedEvent event) {
        rabbitTemplate.convertAndSend(exchangeName, reactionCreatedEventKey, event);
    }

}
