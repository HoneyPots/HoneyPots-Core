package com.honeypot.domain.reaction.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.comment.repository.CommentRepository;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.reaction.dto.ReactionDto;
import com.honeypot.domain.reaction.dto.ReactionRequest;
import com.honeypot.domain.reaction.entity.CommentReaction;
import com.honeypot.domain.reaction.entity.Reaction;
import com.honeypot.domain.reaction.entity.enums.ReactionTarget;
import com.honeypot.domain.reaction.mapper.ReactionMapper;
import com.honeypot.domain.reaction.repository.CommentReactionRepository;
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
public class CommentReactionService {

    private final ReactionMapper reactionMapper;

    private final CommentRepository commentRepository;

    private final CommentReactionRepository commentReactionRepository;

    private final MemberFindService memberFindService;

    @Transactional(readOnly = true)
    public ReactionDto find(@NotNull Long reactionId) {
        Reaction reaction = commentReactionRepository
                .findById(reactionId)
                .orElseThrow(EntityNotFoundException::new);

        ReactionDto dto = reactionMapper.toDto(reaction);
        dto.setAlreadyExists(true);

        return dto;
    }

    @Transactional
    @Validated(InsertContext.class)
    public ReactionDto save(@Valid ReactionRequest request) {
        if (request.getTargetType() != ReactionTarget.COMMENT) {
            throw new IllegalArgumentException("Reaction target must be 'COMMENT'");
        }

        commentRepository.findById(request.getTargetId())
                .orElseThrow(EntityNotFoundException::new);

        Optional<CommentReaction> founded = commentReactionRepository.findByReactorIdAndCommentId(
                request.getReactorId(), request.getTargetId());

        boolean alreadyExists = false;

        Reaction createdOrExisted;
        if (founded.isPresent()) {
            alreadyExists = true;
            createdOrExisted = founded.get();
        } else {
            createdOrExisted = commentReactionRepository.save(reactionMapper.toCommentReactionEntity(request));
        }

        ReactionDto result = reactionMapper.toDto(createdOrExisted);
        result.setAlreadyExists(alreadyExists);

        long reactorId = result.getReactor().getId();
        Member reactor = memberFindService
                .findById(reactorId)
                .orElseThrow(EntityNotFoundException::new);
        result.getReactor().setNickname(reactor.getNickname());

        return result;
    }

    @Transactional
    public void cancel(@NotNull Long memberId, @NotNull Long reactionId) {
        CommentReaction reaction = commentReactionRepository
                .findById(reactionId)
                .orElseThrow(EntityNotFoundException::new);

        if (!reaction.getReactor().getId().equals(memberId)) {
            throw new InvalidAuthorizationException();
        }

        commentReactionRepository.delete(reaction);
    }

}
