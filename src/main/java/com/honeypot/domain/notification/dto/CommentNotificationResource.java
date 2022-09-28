package com.honeypot.domain.notification.dto;

import com.google.common.base.Objects;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentNotificationResource extends NotificationResource {

    private final PostNotificationResource postResource;

    private final Long commentId;

    private final String commenter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentNotificationResource that = (CommentNotificationResource) o;
        return Objects.equal(postResource, that.postResource)
                && Objects.equal(commentId, that.commentId)
                && Objects.equal(commenter, that.commenter);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(postResource, commentId, commenter);
    }

}
