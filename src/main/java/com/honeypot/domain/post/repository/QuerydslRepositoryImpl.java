package com.honeypot.domain.post.repository;

import com.honeypot.domain.member.dto.QWriterDto;
import com.honeypot.domain.post.dto.NormalPostDto;
import com.honeypot.domain.post.dto.QNormalPostDto;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.honeypot.domain.post.entity.QPost.post;
import static com.honeypot.domain.reaction.entity.QReaction.reaction;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.querydsl.jpa.JPAExpressions.selectOne;

@Repository
@RequiredArgsConstructor
public class QuerydslRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<NormalPostDto> findAllPostWithCommentAndReactionCount(Pageable pageable, Long memberId) {
        memberId = memberId == null ? -1 : memberId;

        List<NormalPostDto> result = jpaQueryFactory
                .select(new QNormalPostDto(
                                post.id,
                                post.title,
                                post.content,
                                new QWriterDto(
                                        post.writer.id,
                                        post.writer.nickname
                                ),
                                post.comments.size().castToNum(Long.class),
                                ExpressionUtils.as(
                                        select(reaction.count())
                                                .from(reaction)
                                                .where(reaction.postId.eq(post.id)
                                                        .and(reaction.reactionType.eq(ReactionType.LIKE))),
                                        "likeReactionCount"),
                                ExpressionUtils.as(
                                        select(selectOne())
                                                .from(reaction)
                                                .where(reaction.postId.eq(post.id)
                                                        .and(reaction.reactionType.eq(ReactionType.LIKE))
                                                        .and(reaction.reactor.id.eq(memberId))
                                                )
                                                .exists(),
                                        "isLiked"),
                                ExpressionUtils.as(
                                        select(reaction.id)
                                                .from(reaction)
                                                .where(reaction.postId.eq(post.id)
                                                        .and(reaction.reactionType.eq(ReactionType.LIKE))
                                                        .and(reaction.reactor.id.eq(memberId))
                                                ),
                                        "likeReactionId"),
                                post.createdAt,
                                post.lastModifiedAt
                        )
                )
                .from(post)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sort(pageable.getSort()))
                .fetch();

        long totalCount = jpaQueryFactory
                .selectFrom(post)
                .fetch()
                .size();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private OrderSpecifier<?> sort(Sort sort) {
        for (Sort.Order order : sort) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            if ("createdAt".equals(order.getProperty())) {
                return new OrderSpecifier<>(direction, post.createdAt);
            }
        }
        return null;
    }

}
