package com.honeypot.common.event;

import com.honeypot.domain.comment.dto.CommentDto;
import com.honeypot.domain.notification.dto.CommentNotificationResource;
import com.honeypot.domain.notification.dto.NotificationData;
import com.honeypot.domain.notification.dto.PostNotificationResource;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.service.NotificationSendService;
import com.honeypot.domain.post.dto.SimplePostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventListener {

    private static final String MESSAGE_COMMENT_TO_POST = "'%s'님이 새로운 댓글을 남겼습니다.";

    private final NotificationSendService notificationSendService;

    @Async
    @EventListener
    public void listenCommentCreatedEvent(CommentCreatedEvent event) {
        SimplePostDto targetPost = event.getTargetPost();
        CommentDto createdComment = event.getCreatedComment();
        if (isSameWriter(targetPost, createdComment)) {
            return;
        }

        CommentNotificationResource resource = CommentNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(targetPost.getPostId())
                        .type(targetPost.getPostType())
                        .writer(targetPost.getWriter().getNickname())
                        .build())
                .commentId(createdComment.getCommentId())
                .commenter(createdComment.getWriter().getNickname())
                .build();

        String commentContent = createdComment.getContent();
        String contentMessage = commentContent.substring(0, Math.min(commentContent.length(), 100));
        notificationSendService.send(
                targetPost.getWriter().getId(),
                NotificationData.<CommentNotificationResource>builder()
                        .type(NotificationType.COMMENT_TO_POST)
                        .titleMessage(String.format(MESSAGE_COMMENT_TO_POST, createdComment.getWriter().getNickname()))
                        .contentMessage(contentMessage)
                        .resource(resource)
                        .build()
        );

    }

    private boolean isSameWriter(SimplePostDto post, CommentDto comment) {
        return comment.getWriter().getId().equals(post.getWriter().getId());
    }

}
