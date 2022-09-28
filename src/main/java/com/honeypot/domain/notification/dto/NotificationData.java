package com.honeypot.domain.notification.dto;

import com.google.common.base.Objects;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationData<T extends NotificationResource> {

    private NotificationType type;

    private T resource;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationData<?> that = (NotificationData<?>) o;
        return type == that.type && Objects.equal(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, resource);
    }

}
