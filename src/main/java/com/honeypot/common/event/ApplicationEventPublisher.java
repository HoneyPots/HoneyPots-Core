package com.honeypot.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationEventPublisher {

    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    public void publishEvent(CommentCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishEvent(ReactionCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }

}
