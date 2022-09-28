package com.honeypot.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentNotificationResource extends NotificationResource {

    private final PostNotificationResource postResource;

    private final Long commentId;

    private final String commenter;

}
