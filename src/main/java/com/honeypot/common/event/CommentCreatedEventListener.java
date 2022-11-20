package com.honeypot.common.event;

import com.honeypot.domain.notification.service.NotificationSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventListener {

    private final NotificationSendService notificationSendService;

    @Async
    @EventListener
    public void listenCommentCreatedEvent(CommentCreatedEvent event) {
        notificationSendService.send(event);
    }

}
