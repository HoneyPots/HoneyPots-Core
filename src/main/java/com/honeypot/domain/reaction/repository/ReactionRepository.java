package com.honeypot.domain.reaction.repository;

import com.honeypot.domain.reaction.entity.Reaction;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByReactorIdAndPostIdAndReactionType(Long reactorId, Long postId, ReactionType reactionType);

    Optional<Reaction> findByReactorIdAndCommentIdAndReactionType(Long reactorId, Long commentId, ReactionType reactionType);

    long countByReactionTypeAndPostId(ReactionType reactionType, Long postId);

    long countByReactionTypeAndCommentId(ReactionType reactionType, Long commentId);

    @Query(value = "SELECT CASE WHEN COUNT(*) >=1 THEN true ELSE false END " +
            "FROM reaction r " +
            "WHERE r.reaction_type = 'LIKE' " +
            "AND member_id = :memberId " +
            "AND post_id = :postId",
            nativeQuery = true)
    boolean isLikePost(Long postId, Long memberId);

}
