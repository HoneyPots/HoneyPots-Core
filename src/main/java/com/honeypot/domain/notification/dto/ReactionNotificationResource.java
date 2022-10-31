package com.honeypot.domain.notification.dto;

import com.google.common.base.Objects;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReactionNotificationResource implements NotificationResource {

    private final PostNotificationResource postResource;

    private final Long reactionId;

    private final ReactionType reactionType;

    private final String reactor;

    @Override
    public Long getReferenceId() {
        return reactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionNotificationResource that = (ReactionNotificationResource) o;
        return Objects.equal(postResource, that.postResource)
                && reactionType == that.reactionType
                && Objects.equal(reactor, that.reactor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(postResource, reactionType, reactor);
    }

}
