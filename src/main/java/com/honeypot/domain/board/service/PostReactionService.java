package com.honeypot.domain.board.service;

import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.board.dto.ReactionDto;
import com.honeypot.domain.board.dto.ReactionRequest;
import com.honeypot.domain.board.entity.PostReaction;
import com.honeypot.domain.board.mapper.PostReactionMapper;
import com.honeypot.domain.board.repository.PostReactionRepository;
import com.honeypot.domain.board.repository.PostRepository;
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
public class PostReactionService {

    private final PostReactionMapper postReactionMapper;

    private final PostReactionRepository postReactionRepository;

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    @Transactional
    @Validated(InsertContext.class)
    public ReactionDto save(@Valid ReactionRequest request) {
        postRepository.findById(request.getTargetId())
                .orElseThrow(EntityNotFoundException::new);

        boolean alreadyExists = false;
        Optional<PostReaction> existed = postReactionRepository
                .findByReactorIdAndPostIdAndReactionType(
                        request.getReactorId(), request.getTargetId(), request.getReactionType());

        PostReaction createdOrExisted;
        if (existed.isPresent()) {
            alreadyExists = true;
            createdOrExisted = existed.get();
        } else {
            createdOrExisted = postReactionRepository.save(postReactionMapper.toEntity(request));
        }

        ReactionDto result = postReactionMapper.toDto(createdOrExisted);
        result.setAlreadyExists(alreadyExists);

        long reactorId = result.getReactor().getId();
        Member reactor = memberRepository
                .findById(reactorId)
                .orElseThrow(EntityNotFoundException::new);

        result.getReactor().setNickname(reactor.getNickname());

        return result;
    }

}
