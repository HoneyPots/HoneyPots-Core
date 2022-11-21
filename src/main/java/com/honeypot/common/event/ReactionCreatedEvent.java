package com.honeypot.common.event;

import com.honeypot.domain.post.dto.SimplePostDto;
import com.honeypot.domain.post.entity.Post;
import com.honeypot.domain.reaction.dto.ReactionDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ReactionCreatedEvent {

    private final SimplePostDto targetPost;

    private final ReactionDto createdReaction;

}
