package com.honeypot.domain.post.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.comment.repository.CommentRepository;
import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.file.FileUploadService;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import com.honeypot.domain.post.dto.NormalPostDto;
import com.honeypot.domain.post.dto.NormalPostUploadRequest;
import com.honeypot.domain.file.PostFileUploadRequest;
import com.honeypot.domain.post.dto.PostDto;
import com.honeypot.domain.post.entity.NormalPost;
import com.honeypot.domain.post.entity.enums.PostType;
import com.honeypot.domain.post.mapper.NormalPostMapper;
import com.honeypot.domain.post.repository.NormalPostRepository;
import com.honeypot.domain.post.repository.NormalPostQuerydslRepositoryImpl;
import com.honeypot.domain.reaction.repository.ReactionRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class NormalPostService implements PostCrudService<NormalPostDto, NormalPostUploadRequest> {

    private final NormalPostQuerydslRepositoryImpl querydslRepository;

    private final NormalPostMapper normalPostMapper;

    private final NormalPostRepository normalPostRepository;

    private final MemberRepository memberRepository;

    private final FileUploadService fileUploadService;

    @Override
    public PostType getPostType() {
        return PostType.NORMAL;
    }

    @Transactional(readOnly = true)
    public Page<NormalPostDto> pageList(Pageable pageable, Long memberId) {
        Page<NormalPostDto> result = querydslRepository.findAllPostWithCommentAndReactionCount(pageable, memberId);
        return new PageImpl<>(
                result.getContent(),
                pageable,
                result.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public NormalPostDto find(@NotNull Long postId, Long memberId) {
        NormalPost normalPost = normalPostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);
        return querydslRepository.findPostDetailById(postId, memberId);
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

        List<PostFileUploadRequest> attachedFiles = request.getAttachedFiles();
        if (attachedFiles != null) {
            List<AttachedFileResponse> files = attachedFiles.stream()
                    .filter(PostFileUploadRequest::isWillBeUploaded)
                    .peek(f -> f.setLinkPostId(result.getPostId()))
                    .map(fileUploadService::linkFileWithPost)
                    .toList();

            result.setAttachedFiles(files);
        }

        return result;
    }

    @Transactional
    @Validated(InsertContext.class)
    public NormalPostDto update(Long postId, @Valid NormalPostUploadRequest uploadRequest) {
        NormalPost normalPost = normalPostRepository
                .findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        if (!normalPost.getWriter().getId().equals(uploadRequest.getWriterId())) {
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

        if (!normalPost.getWriter().getId().equals(memberId)) {
            throw new InvalidAuthorizationException();
        }

        normalPostRepository.delete(normalPost);
    }

}
