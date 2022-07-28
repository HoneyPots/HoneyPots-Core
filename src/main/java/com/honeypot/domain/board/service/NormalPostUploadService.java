package com.honeypot.domain.board.service;

import com.honeypot.common.validation.InsertContext;
import com.honeypot.domain.board.dto.NormalPostDto;
import com.honeypot.domain.board.dto.NormalPostUploadRequest;
import com.honeypot.domain.board.entity.NormalPost;
import com.honeypot.domain.board.mapper.NormalPostMapper;
import com.honeypot.domain.board.repository.PostRepository;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Validated
public class NormalPostUploadService {

    private final NormalPostMapper normalPostMapper;

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    @Transactional
    @Validated(InsertContext.class)
    public NormalPostDto upload(@Valid NormalPostUploadRequest request) {
        NormalPost created = postRepository.save(normalPostMapper.toEntity(request));

        long writerId = created.getWriter().getId();
        Member writer = memberRepository
                .findById(writerId)
                .orElseThrow(EntityNotFoundException::new);

        NormalPostDto result = normalPostMapper.toDto(created);
        result.getWriter().setNickname(writer.getNickname());

        return result;
    }

}
