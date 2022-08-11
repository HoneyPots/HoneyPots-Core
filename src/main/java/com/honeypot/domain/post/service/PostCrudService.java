package com.honeypot.domain.post.service;

import com.honeypot.domain.post.dto.PostDto;
import com.honeypot.domain.post.dto.PostUploadRequest;
import com.honeypot.domain.post.entity.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface PostCrudService<T extends PostDto, U extends PostUploadRequest> {

    PostType getPostType();

    Page<T> pageList(Pageable pageable, Long memberId);

    Page<T> pageListByMemberId(Pageable pageable, Long memberId);

    T find(@NotNull Long postId, Long memberId);

    T upload(@Valid U request);

    T update(Long postId, @Valid U uploadRequest);

    void delete(Long postId, @NotNull Long memberId);

}
