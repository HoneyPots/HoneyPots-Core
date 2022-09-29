package com.honeypot.domain.notification.dto;

import com.google.common.base.Objects;
import com.honeypot.domain.post.entity.enums.PostType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostNotificationResource implements NotificationResource {

    private final Long id;

    private final PostType type;

    private final String writer;

    @Override
    public Long getReferenceId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostNotificationResource that = (PostNotificationResource) o;
        return Objects.equal(id, that.id)
                && type == that.type
                && Objects.equal(writer, that.writer);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, type, writer);
    }

}
