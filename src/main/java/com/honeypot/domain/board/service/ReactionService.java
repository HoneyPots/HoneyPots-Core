package com.honeypot.domain.board.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.board.dto.ReactionDto;
import com.honeypot.domain.board.dto.ReactionRequest;
import com.honeypot.domain.board.entity.Reaction;
import com.honeypot.domain.board.enums.ReactionTarget;
import com.honeypot.domain.board.mapper.ReactionMapper;
import com.honeypot.domain.board.repository.*;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
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
public class ReactionService {

    private final ReactionMapper reactionMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final PostReactionRepository postReactionRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final MemberRepository memberRepository;

    public ReactionDto find(@NotNull Long reactionId) {
        Reaction reaction = reactionRepository
                .findById(reactionId)
                .orElseThrow(EntityNotFoundException::new);

        ReactionDto dto = reactionMapper.toDto(reaction);
        dto.setAlreadyExists(true);

        return dto;
    }

    @Transactional
    @Validated(InsertContext.class)
    public ReactionDto save(@Valid ReactionRequest request) {
        Optional<Reaction> founded = findReaction(request);

        boolean alreadyExists = false;

        Reaction createdOrExisted;
        if (founded.isPresent()) {
            alreadyExists = true;
            createdOrExisted = founded.get();
        } else {
            createdOrExisted = saveReaction(request);
        }

        ReactionDto result = reactionMapper.toDto(createdOrExisted);
        result.setAlreadyExists(alreadyExists);

        long reactorId = result.getReactor().getId();
        Member reactor = memberRepository
                .findById(reactorId)
                .orElseThrow(EntityNotFoundException::new);

        result.getReactor().setNickname(reactor.getNickname());

        return result;
    }

    @Transactional
    public void cancel(@NotNull Long memberId, @NotNull Long reactionId) {
        Reaction reaction = reactionRepository
                .findById(reactionId)
                .orElseThrow(EntityNotFoundException::new);

        if (!reaction.getReactor().getId().equals(memberId)) {
            throw new InvalidAuthorizationException();
        }

        reactionRepository.delete(reaction);
    }

    private Optional<Reaction> findReaction(ReactionRequest request) {
        if (request.getTargetType() == ReactionTarget.POST) {
            return findPostReaction(request);
        } else if (request.getTargetType() == ReactionTarget.COMMENT) {
            return findCommentReaction(request);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Optional<Reaction> findPostReaction(ReactionRequest request) {
        postRepository.findById(request.getTargetId())
                .orElseThrow(EntityNotFoundException::new);

        return reactionRepository.findByReactorIdAndPostIdAndReactionType(
                request.getReactorId(), request.getTargetId(), request.getReactionType());
    }

    private Optional<Reaction> findCommentReaction(ReactionRequest request) {
        commentRepository.findById(request.getTargetId())
                .orElseThrow(EntityNotFoundException::new);

        return reactionRepository.findByReactorIdAndCommentIdAndReactionType(
                request.getReactorId(), request.getTargetId(), request.getReactionType());
    }

    private Reaction saveReaction(ReactionRequest request) {
        if (request.getTargetType() == ReactionTarget.POST) {
            return postReactionRepository.save(reactionMapper.toPostReactionEntity(request));
        } else if (request.getTargetType() == ReactionTarget.COMMENT) {
            return commentReactionRepository.save(reactionMapper.toCommentReactionEntity(request));
        } else {
            throw new IllegalArgumentException();
        }
    }
    
}
