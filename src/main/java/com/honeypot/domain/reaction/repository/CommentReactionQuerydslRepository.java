package com.honeypot.domain.reaction.repository;

import com.honeypot.domain.reaction.entity.CommentReaction;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface CommentReactionQuerydslRepository {

    Optional<CommentReaction> findByReactorIdAndCommentId(@NotNull Long reactorId, @NotNull Long commentId);

}
