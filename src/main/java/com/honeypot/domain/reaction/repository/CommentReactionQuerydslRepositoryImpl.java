package com.honeypot.domain.reaction.repository;

import com.honeypot.domain.reaction.entity.CommentReaction;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static com.honeypot.domain.reaction.entity.QCommentReaction.commentReaction;

@Repository
@RequiredArgsConstructor
public class CommentReactionQuerydslRepositoryImpl implements CommentReactionQuerydslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<CommentReaction> findByReactorIdAndCommentId(@NotNull Long reactorId, @NotNull Long commentId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(commentReaction)
                        .where(commentReaction.comment.id.eq(commentId)
                                .and(commentReaction.reactor.id.eq(reactorId))
                        )
                        .fetchOne()
        );
    }

}
