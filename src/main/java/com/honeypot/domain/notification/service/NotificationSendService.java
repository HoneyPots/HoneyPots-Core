package com.honeypot.domain.notification.service;

import com.honeypot.common.event.CommentCreatedEvent;
import com.honeypot.common.event.ReactionCreatedEvent;

public interface NotificationSendService {

    void send(CommentCreatedEvent commentCreatedEvent);

    void send(ReactionCreatedEvent reactionCreatedEvent);

}
