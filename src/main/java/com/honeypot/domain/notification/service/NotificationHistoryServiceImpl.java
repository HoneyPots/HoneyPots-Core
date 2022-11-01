package com.honeypot.domain.notification.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.comment.repository.CommentRepository;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.dto.*;
import com.honeypot.domain.notification.entity.Notification;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.mapper.NotificationMapper;
import com.honeypot.domain.notification.repository.NotificationRepository;
import com.honeypot.domain.reaction.entity.PostReaction;
import com.honeypot.domain.reaction.repository.PostReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class NotificationHistoryServiceImpl implements NotificationHistoryService {

    private final MemberFindService memberFindService;

    private final NotificationMapper notificationMapper;

    private final NotificationRepository notificationRepository;

    private final CommentRepository commentRepository;

    private final PostReactionRepository postReactionRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationResource findNotificationResourceById(@NotNull Long notificationId) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        Member member = memberFindService.findById(memberId).orElseThrow(EntityNotFoundException::new);

        // TODO Check member's role and authority
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(EntityNotFoundException::new);
        if (!notification.getMember().getId().equals(member.getId())) {
            throw new InvalidAuthorizationException();
        }

        Long referenceId = notification.getReferenceId();
        NotificationResource result = null;
        if (notification.getType() == NotificationType.COMMENT_TO_POST) {
            Optional<Comment> commentOptional = commentRepository.findById(referenceId);
            if (commentOptional.isPresent()) {
                Comment comment = commentOptional.get();
                result = CommentNotificationResource.builder()
                        .postResource(
                                PostNotificationResource.builder()
                                        .id(comment.getPost().getId())
                                        .writer(comment.getPost().getWriter().getNickname())
                                        .type(comment.getPost().getType())
                                        .build()
                        )
                        .commentId(comment.getId())
                        .commenter(comment.getWriter().getNickname())
                        .build();
            }
        } else if (notification.getType() == NotificationType.LIKE_REACTION_TO_POST) {
            Optional<PostReaction> reactionOptional = postReactionRepository.findById(referenceId);
            if (reactionOptional.isPresent()) {
                PostReaction reaction = reactionOptional.get();
                result = ReactionNotificationResource.builder()
                        .postResource(
                                PostNotificationResource.builder()
                                        .id(reaction.getPost().getId())
                                        .writer(reaction.getPost().getWriter().getNickname())
                                        .type(reaction.getPost().getType())
                                        .build()
                        )
                        .reactionId(reaction.getId())
                        .reactionType(reaction.getReactionType())
                        .reactor(reaction.getReactor().getNickname())
                        .build();
            }
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> findByMemberWithPagination(
            @NotNull Long memberId,
            @NotNull Pageable pageable
    ) {
        Member member = memberFindService.findById(memberId).orElseThrow(EntityNotFoundException::new);
        Page<Notification> page = notificationRepository.findByMember(member, pageable);

        return new PageImpl<>(
                page.getContent()
                        .stream()
                        .map(notificationMapper::toDto)
                        .toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

}
