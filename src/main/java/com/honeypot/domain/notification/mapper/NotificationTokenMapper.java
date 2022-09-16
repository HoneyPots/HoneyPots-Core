package com.honeypot.domain.notification.mapper;

import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.entity.NotificationToken;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationTokenMapper {

    @Mapping(target = "id", source = "notificationTokenId")
    @Mapping(target = "member.id", source = "memberId")
    @Mapping(target = "deviceToken", source = "deviceToken")
    @Mapping(target = "clientType", source = "clientType")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    NotificationToken toEntity(NotificationTokenDto dto);

    @InheritInverseConfiguration
    NotificationTokenDto toDto(NotificationToken entity);

}