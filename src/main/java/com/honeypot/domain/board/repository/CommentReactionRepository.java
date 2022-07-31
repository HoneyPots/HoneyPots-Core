package com.honeypot.domain.board.repository;

import com.honeypot.domain.board.entity.CommentReaction;
import com.honeypot.domain.board.entity.PostReaction;
import com.honeypot.domain.board.entity.Reaction;
import com.honeypot.domain.board.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    Optional<CommentReaction> findByReactorIdAndCommentIdAndReactionType(Long reactorId, Long commentId, ReactionType reactionType);

}
