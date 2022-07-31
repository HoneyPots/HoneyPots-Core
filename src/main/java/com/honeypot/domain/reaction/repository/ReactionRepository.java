package com.honeypot.domain.reaction.repository;

import com.honeypot.domain.reaction.entity.Reaction;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByReactorIdAndPostIdAndReactionType(Long reactorId, Long postId, ReactionType reactionType);

    Optional<Reaction> findByReactorIdAndCommentIdAndReactionType(Long reactorId, Long commentId, ReactionType reactionType);

}
