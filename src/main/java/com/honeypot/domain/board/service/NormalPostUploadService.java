package com.honeypot.domain.board.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.board.dto.NormalPostDto;
import com.honeypot.domain.board.dto.NormalPostUploadRequest;
import com.honeypot.domain.board.entity.NormalPost;
import com.honeypot.domain.board.mapper.NormalPostMapper;
import com.honeypot.domain.board.repository.NormalPostRepository;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor
@Validated
public class NormalPostUploadService {

    private final NormalPostMapper normalPostMapper;

    private final NormalPostRepository normalPostRepository;

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<NormalPostDto> pageList(Pageable pageable) {
        Page<NormalPost> result = normalPostRepository.findAll(pageable);
        return new PageImpl<>(
                normalPostMapper.toDto(result.getContent()),
                pageable,
                result.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public NormalPostDto find(@NotNull Long postId) {
        NormalPost result = normalPostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        return normalPostMapper.toDto(result);
    }

    @Transactional
    @Validated(InsertContext.class)
    public NormalPostDto upload(@Valid NormalPostUploadRequest request) {
        NormalPost created = normalPostRepository.save(normalPostMapper.toEntity(request));

        long writerId = created.getWriter().getId();
        Member writer = memberRepository
                .findById(writerId)
                .orElseThrow(EntityNotFoundException::new);

        NormalPostDto result = normalPostMapper.toDto(created);
        result.getWriter().setNickname(writer.getNickname());

        return result;
    }

    @Transactional
    public NormalPostDto update(Long postId, NormalPostUploadRequest uploadRequest) {
        NormalPost normalPost = normalPostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        if(!normalPost.getWriter().getId().equals(uploadRequest.getWriterId())) {
            throw new InvalidAuthorizationException();
        }

        normalPost.setTitle(uploadRequest.getTitle());
        normalPost.setContent(uploadRequest.getContent());

        return normalPostMapper.toDto(normalPostRepository.save(normalPost));
    }

    @Transactional
    public void delete(Long postId, @NotNull Long memberId) {
        NormalPost normalPost = normalPostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        if(!normalPost.getWriter().getId().equals(memberId)) {
            throw new InvalidAuthorizationException();
        }

        normalPostRepository.delete(normalPost);
    }

}