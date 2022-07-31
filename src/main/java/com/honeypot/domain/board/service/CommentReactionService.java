package com.honeypot.domain.board.service;

import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.board.dto.ReactionDto;
import com.honeypot.domain.board.dto.ReactionRequest;
import com.honeypot.domain.board.entity.CommentReaction;
import com.honeypot.domain.board.mapper.CommentReactionMapper;
import com.honeypot.domain.board.repository.CommentReactionRepository;
import com.honeypot.domain.board.repository.CommentRepository;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class CommentReactionService {

    private final CommentReactionMapper commentReactionMapper;

    private final CommentReactionRepository commentReactionRepository;

    private final CommentRepository commentRepository;

    private final MemberRepository memberRepository;

    @Transactional
    @Validated(InsertContext.class)
    public ReactionDto save(@Valid ReactionRequest request) {
        commentRepository.findById(request.getTargetId())
                .orElseThrow(EntityNotFoundException::new);

        boolean alreadyExists = false;
        Optional<CommentReaction> existed = commentReactionRepository
                .findByReactorIdAndCommentIdAndReactionType(
                        request.getReactorId(), request.getTargetId(), request.getReactionType());

        CommentReaction createdOrExisted;
        if (existed.isPresent()) {
            alreadyExists = true;
            createdOrExisted = existed.get();
        } else {
            createdOrExisted = commentReactionRepository.save(commentReactionMapper.toEntity(request));
        }

        ReactionDto result = commentReactionMapper.toDto(createdOrExisted);
        result.setAlreadyExists(alreadyExists);

        long reactorId = result.getReactor().getId();
        Member reactor = memberRepository
                .findById(reactorId)
                .orElseThrow(EntityNotFoundException::new);

        result.getReactor().setNickname(reactor.getNickname());

        return result;
    }

}
