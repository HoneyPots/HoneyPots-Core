package com.honeypot.domain.post.repository;

import com.honeypot.domain.file.QAttachedFileResponse;
import com.honeypot.domain.member.dto.QWriterDto;
import com.honeypot.domain.post.dto.PostDto;
import com.honeypot.domain.post.dto.QGroupBuyingPostDto;
import com.honeypot.domain.post.dto.QNormalPostDto;
import com.honeypot.domain.post.dto.QUsedTradePostDto;
import com.honeypot.domain.post.entity.enums.PostType;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.Expressions;

import static com.honeypot.domain.file.QFile.file;
import static com.honeypot.domain.post.entity.QGroupBuyingPost.groupBuyingPost;
import static com.honeypot.domain.post.entity.QPost.post;
import static com.honeypot.domain.post.entity.QUsedTradePost.usedTradePost;
import static com.honeypot.domain.reaction.entity.QReaction.reaction;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.querydsl.jpa.JPAExpressions.selectOne;

public class PostQuerydslSelectStatementFactory {

    public static Expression<? extends PostDto> of(PostType postType, Long memberId, String s3Domain) {
        if (postType == PostType.NORMAL) {
            return new QNormalPostDto(
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
                    new QAttachedFileResponse(
                            file.id.min().coalesce(Expressions.nullExpression()),
                            file.filePath.min().prepend(s3Domain).coalesce(Expressions.nullExpression())
                    ).skipNulls(),
                    post.createdAt,
                    post.lastModifiedAt
            );
        } else if (postType == PostType.USED_TRADE) {
            return new QUsedTradePostDto(
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
                    new QAttachedFileResponse(
                            file.id.min().coalesce(Expressions.nullExpression()),
                            file.filePath.min().prepend(s3Domain).coalesce(Expressions.nullExpression())
                    ).skipNulls(),
                    post.createdAt,
                    post.lastModifiedAt,
                    usedTradePost.goodsPrice,
                    usedTradePost.tradeType,
                    usedTradePost.tradeStatus,
                    usedTradePost.chatRoomLink
            );
        } else if (postType == PostType.GROUP_BUYING) {
            return new QGroupBuyingPostDto(
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
                    new QAttachedFileResponse(
                            file.id.min().coalesce(Expressions.nullExpression()),
                            file.filePath.min().prepend(s3Domain).coalesce(Expressions.nullExpression())
                    ).skipNulls(),
                    post.createdAt,
                    post.lastModifiedAt,
                    groupBuyingPost.category,
                    groupBuyingPost.groupBuyingStatus,
                    groupBuyingPost.chatRoomLink,
                    groupBuyingPost.deadline
            );
        } else {
            throw new IllegalArgumentException("Invalid postType");
        }
    }
}
