package com.honeypot.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionCreatedEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(ReactionCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }

}
