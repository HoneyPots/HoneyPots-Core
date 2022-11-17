package com.honeypot.common.event;

import com.honeypot.domain.comment.dto.CommentDto;
import com.honeypot.domain.post.entity.Post;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class CommentCreatedEvent {

    private final Post targetPost;

    private final CommentDto createdComment;

}
