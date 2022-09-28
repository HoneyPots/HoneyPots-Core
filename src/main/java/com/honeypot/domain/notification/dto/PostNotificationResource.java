package com.honeypot.domain.notification.dto;

import com.honeypot.domain.post.entity.enums.PostType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostNotificationResource extends NotificationResource {

    private final Long id;

    private final PostType type;

    private final String writer;

}
