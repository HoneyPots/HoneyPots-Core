package com.honeypot.domain.reaction.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.service.NotificationSendService;
import com.honeypot.domain.post.entity.Post;
import com.honeypot.domain.post.repository.PostRepository;
import com.honeypot.domain.reaction.dto.ReactionDto;
import com.honeypot.domain.reaction.dto.ReactionRequest;
import com.honeypot.domain.reaction.entity.Reaction;
import com.honeypot.domain.reaction.entity.enums.ReactionTarget;
import com.honeypot.domain.reaction.mapper.ReactionMapper;
import com.honeypot.domain.reaction.repository.PostReactionRepository;
import com.honeypot.domain.reaction.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class PostReactionService {

    private final ReactionMapper reactionMapper;

    private final PostRepository postRepository;

    private final ReactionRepository reactionRepository;

    private final PostReactionRepository postReactionRepository;

    private final MemberFindService memberFindService;

    private final NotificationSendService notificationSendService;

    @Transactional(readOnly = true)
    public ReactionDto find(@NotNull Long reactionId) {
        Reaction reaction = postReactionRepository
                .findById(reactionId)
                .orElseThrow(EntityNotFoundException::new);

        ReactionDto dto = reactionMapper.toDto(reaction);
        dto.setAlreadyExists(true);

        return dto;
    }

    @Transactional
    @Validated(InsertContext.class)
    public ReactionDto save(@Valid ReactionRequest request) {
        if (request.getTargetType() != ReactionTarget.POST) {
            throw new IllegalArgumentException("Reaction target must be 'POST'");
        }

        Post targetPost = postRepository.findById(request.getTargetId()).orElseThrow(EntityNotFoundException::new);

        Optional<Reaction> founded = reactionRepository.findByReactorIdAndPostIdAndReactionType(
                request.getReactorId(), request.getTargetId(), request.getReactionType());

        boolean alreadyExists = false;

        Reaction createdOrExisted;
        if (founded.isPresent()) {
            alreadyExists = true;
            createdOrExisted = founded.get();
        } else {
            createdOrExisted = postReactionRepository.save(reactionMapper.toPostReactionEntity(request));
        }

        ReactionDto result = reactionMapper.toDto(createdOrExisted);
        result.setAlreadyExists(alreadyExists);

        long reactorId = result.getReactor().getId();
        Member reactor = memberFindService.findById(reactorId).orElseThrow(EntityNotFoundException::new);
        result.getReactor().setNickname(reactor.getNickname());

        // Async tasks
        Long targetPostWriterId = targetPost.getWriter().getId();
        if (!request.getReactorId().equals(targetPostWriterId) && !alreadyExists) {
            notificationSendService.send(targetPostWriterId, NotificationType.LIKE_REACTION_TO_MY_POST);
        }

        return result;
    }

    @Transactional
    public void cancel(@NotNull Long memberId, @NotNull Long reactionId) {
        Reaction reaction = reactionRepository
                .findByIdAndTargetType(reactionId, ReactionTarget.POST)
                .orElseThrow(EntityNotFoundException::new);

        if (!reaction.getReactor().getId().equals(memberId)) {
            throw new InvalidAuthorizationException();
        }

        reactionRepository.delete(reaction);
    }

}
