package com.honeypot.domain.comment.service;

import com.honeypot.common.event.ApplicationEventPublisher;
import com.honeypot.common.event.CommentCreatedEvent;
import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.comment.dto.CommentDto;
import com.honeypot.domain.comment.dto.CommentUploadRequest;
import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.comment.mapper.CommentMapper;
import com.honeypot.domain.comment.repository.CommentRepository;
import com.honeypot.domain.member.dto.MemberDto;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.post.dto.SimplePostDto;
import com.honeypot.domain.post.entity.Post;
import com.honeypot.domain.post.repository.PostRepository;
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
public class CommentService {

    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final MemberFindService memberFindService;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<CommentDto> pageList(@NotNull Long postId, Pageable pageable) {
        Page<Comment> result = commentRepository.findAllByPostId(postId, pageable);
        return new PageImpl<>(
                commentMapper.toDto(result.getContent()),
                pageable,
                result.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public CommentDto find(@NotNull Long postId, @NotNull Long commentId) {
        postRepository.findById(postId)
                .orElseThrow(EntityNotFoundException::new);

        Comment result = commentRepository
                .findById(commentId)
                .orElseThrow(EntityNotFoundException::new);

        return commentMapper.toDto(result);
    }

    @Transactional
    @Validated(InsertContext.class)
    public CommentDto save(@Valid CommentUploadRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(EntityNotFoundException::new);

        Comment created = commentRepository.save(commentMapper.toEntity(request));

        long writerId = created.getWriter().getId();
        MemberDto writer = memberFindService
                .findById(writerId)
                .orElseThrow(EntityNotFoundException::new);

        CommentDto result = commentMapper.toDto(created);
        result.getWriter().setNickname(writer.getNickname());

        // Async tasks
        eventPublisher.publishEvent(new CommentCreatedEvent(SimplePostDto.toDto(post), result));

        return result;
    }

    @Transactional
    @Validated(InsertContext.class)
    public CommentDto update(Long commentId, CommentUploadRequest uploadRequest) {
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(EntityNotFoundException::new);

        if (!comment.getWriter().getId().equals(uploadRequest.getWriterId())) {
            throw new InvalidAuthorizationException();
        }

        comment.setContent(uploadRequest.getContent());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public void delete(Long commentId, @NotNull Long memberId) {
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(EntityNotFoundException::new);

        if (!comment.getWriter().getId().equals(memberId)) {
            throw new InvalidAuthorizationException();
        }

        commentRepository.delete(comment);
    }

}
