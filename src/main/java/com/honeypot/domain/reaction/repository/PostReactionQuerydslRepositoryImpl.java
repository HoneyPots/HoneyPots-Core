package com.honeypot.domain.reaction.repository;

import com.honeypot.domain.reaction.entity.PostReaction;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static com.honeypot.domain.reaction.entity.QPostReaction.postReaction;

@Repository
@RequiredArgsConstructor
public class PostReactionQuerydslRepositoryImpl implements PostReactionQuerydslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<PostReaction> findByReactorIdAndPostId(@NotNull Long reactorId, @NotNull Long postId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(postReaction)
                        .where(postReaction.post.id.eq(postId)
                                .and(postReaction.reactor.id.eq(reactorId))
                        )
                        .fetchOne()
        );
    }

}
