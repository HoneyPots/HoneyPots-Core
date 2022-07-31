package com.honeypot.domain.board.repository;

import com.honeypot.domain.board.entity.Reaction;
import com.honeypot.domain.board.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByReactorIdAndPostIdAndReactionType(Long reactorId, Long postId, ReactionType reactionType);

    Optional<Reaction> findByReactorIdAndCommentIdAndReactionType(Long reactorId, Long commentId, ReactionType reactionType);

}
