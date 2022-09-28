package com.honeypot.domain.notification.dto;

import com.honeypot.domain.reaction.entity.enums.ReactionType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReactionNotificationResource extends NotificationResource {

    private final PostNotificationResource postResource;

    private final ReactionType reactionType;

    private final String reactor;

}
