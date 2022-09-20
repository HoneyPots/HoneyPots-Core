package com.honeypot.domain.notification.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    COMMENT_TO_MY_POST("꿀단지", "내 글에 새로운 댓글이 작성되었습니다."),
    LIKE_REACTION_TO_MY_POST("꿀단지", "내 글을 좋아합니다.");

    private final String title;

    private final String body;

}
