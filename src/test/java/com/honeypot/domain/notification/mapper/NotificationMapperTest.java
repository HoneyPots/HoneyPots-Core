package com.honeypot.domain.notification.mapper;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.notification.dto.NotificationDto;
import com.honeypot.domain.notification.entity.Notification;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationMapperTest {

    private final NotificationMapper mapper = Mappers.getMapper(NotificationMapper.class);

    @Test
    void toDto() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.of(2022, 9, 16, 3, 0, 0);
        Notification entity = Notification.builder()
                .id(1L)
                .member(Member.builder().id(1L).build())
                .titleMessage("this is test message")
                .contentMessage("this is test message")
                .type(NotificationType.LIKE_REACTION_TO_POST)
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        // Act
        NotificationDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getNotificationId());
        assertEquals(entity.getTitleMessage(), dto.getTitleMessage());
        assertEquals(entity.getContentMessage(), dto.getContentMessage());
        assertEquals(entity.getType(), dto.getType());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getLastModifiedAt());
    }

}