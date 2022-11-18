package com.honeypot.common.event;

import com.honeypot.domain.notification.dto.NotificationData;
import com.honeypot.domain.notification.dto.PostNotificationResource;
import com.honeypot.domain.notification.dto.ReactionNotificationResource;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.service.NotificationSendService;
import com.honeypot.domain.post.dto.SimplePostDto;
import com.honeypot.domain.post.entity.Post;
import com.honeypot.domain.reaction.dto.ReactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionCreatedEventListener {

    private static final String MESSAGE_LIKE_REACTION_TO_POST = "'%s'님이 게시글을 좋아합니다.";

    private final NotificationSendService notificationSendService;

    @Async
    @EventListener
    public void listenReactionCreatedEvent(ReactionCreatedEvent event) {
        SimplePostDto targetPost = event.getTargetPost();
        ReactionDto createdReaction = event.getCreatedReaction();
        if (isSameReactor(targetPost, createdReaction) || createdReaction.isAlreadyExists()) {
            return;
        }

        ReactionNotificationResource resource = ReactionNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(targetPost.getPostId())
                        .type(targetPost.getPostType())
                        .writer(targetPost.getWriter().getNickname())
                        .build())
                .reactionId(createdReaction.getReactionId())
                .reactionType(createdReaction.getReactionType())
                .reactor(createdReaction.getReactor().getNickname())
                .build();

        String postTitle = targetPost.getTitle();
        String contentMessage = postTitle.substring(0, Math.min(postTitle.length(), 100));
        notificationSendService.send(
                targetPost.getWriter().getId(),
                NotificationData.<ReactionNotificationResource>builder()
                        .type(NotificationType.LIKE_REACTION_TO_POST)
                        .titleMessage(String.format(MESSAGE_LIKE_REACTION_TO_POST, createdReaction.getReactor().getNickname()))
                        .contentMessage(contentMessage)
                        .resource(resource)
                        .build()
        );
    }


    private boolean isSameReactor(SimplePostDto post, ReactionDto reaction) {
        return reaction.getReactor().getId().equals(post.getWriter().getId());
    }

}
