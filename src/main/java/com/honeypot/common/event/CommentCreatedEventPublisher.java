package com.honeypot.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(CommentCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }

}
