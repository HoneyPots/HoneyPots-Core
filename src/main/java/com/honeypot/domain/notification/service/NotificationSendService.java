package com.honeypot.domain.notification.service;

import com.honeypot.common.event.CommentCreatedEvent;
import com.honeypot.common.event.ReactionCreatedEvent;
import com.honeypot.domain.notification.dto.NotificationData;
import com.honeypot.domain.notification.dto.NotificationResource;

public interface NotificationSendService {

    <T extends NotificationResource> void send(Long memberId, NotificationData<T> data);

    void send(CommentCreatedEvent commentCreatedEvent);

    void send(ReactionCreatedEvent reactionCreatedEvent);

}
