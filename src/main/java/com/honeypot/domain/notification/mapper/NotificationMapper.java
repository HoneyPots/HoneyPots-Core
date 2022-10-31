package com.honeypot.domain.notification.mapper;

import com.honeypot.domain.notification.dto.NotificationDto;
import com.honeypot.domain.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "notificationId", source = "id")
    @Mapping(target = "titleMessage", source = "titleMessage")
    @Mapping(target = "contentMessage", source = "contentMessage")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    NotificationDto toDto(Notification entity);

}