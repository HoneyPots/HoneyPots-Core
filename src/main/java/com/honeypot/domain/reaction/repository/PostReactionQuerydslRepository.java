package com.honeypot.domain.reaction.repository;

import com.honeypot.domain.reaction.entity.PostReaction;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface PostReactionQuerydslRepository {

    Optional<PostReaction> findByReactorIdAndPostId(@NotNull Long reactorId, @NotNull Long postId);

}
