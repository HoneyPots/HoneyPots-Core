package com.honeypot.domain.post.repository;

import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.file.QAttachedFileResponse;
import com.honeypot.domain.member.dto.QWriterDto;
import com.honeypot.domain.post.dto.GroupBuyingPostDto;
import com.honeypot.domain.post.dto.QGroupBuyingPostDto;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.honeypot.domain.file.QFile.file;
import static com.honeypot.domain.member.entity.QMember.member;
import static com.honeypot.domain.post.entity.QGroupBuyingPost.groupBuyingPost;
import static com.honeypot.domain.post.entity.QPost.post;
import static com.honeypot.domain.reaction.entity.QReaction.reaction;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.querydsl.jpa.JPAExpressions.selectOne;

@Repository
@RequiredArgsConstructor
public class GroupBuyingPostQuerydslRepository {

    @Value("${cloud.aws.s3.domain}")
    private String s3Domain;

    private final JPAQueryFactory jpaQueryFactory;

    public Page<GroupBuyingPostDto> findAllPostWithCommentAndReactionCount(Pageable pageable, Long memberId) {
        memberId = memberId == null ? -1 : memberId;

        List<GroupBuyingPostDto> result = jpaQueryFactory
                .select(getQGroupBuyingPostDtoSelectStatement(memberId))
                .from(post)
                .innerJoin(groupBuyingPost).on(post.id.eq(groupBuyingPost.id))
                .leftJoin(file).on(post.id.eq(file.post.id))
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sort(pageable.getSort()))
                .groupBy(post.id)
                .fetch();

        long totalCount = jpaQueryFactory
                .selectFrom(post)
                .innerJoin(groupBuyingPost).on(post.id.eq(groupBuyingPost.id))
                .fetch()
                .size();

        return new PageImpl<>(result, pageable, totalCount);
    }

    public Page<GroupBuyingPostDto> findAllPostWithCommentAndReactionCountByMemberId(Pageable pageable, Long memberId) {
        List<GroupBuyingPostDto> result = jpaQueryFactory
                .select(getQGroupBuyingPostDtoSelectStatement(memberId))
                .from(post)
                .innerJoin(groupBuyingPost).on(post.id.eq(groupBuyingPost.id))
                .innerJoin(member).on(post.writer.id.eq(memberId))
                .leftJoin(file).on(post.id.eq(file.post.id))
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sort(pageable.getSort()))
                .groupBy(post.id)
                .fetch();

        long totalCount = jpaQueryFactory
                .select(post.id)
                .from(post)
                .innerJoin(groupBuyingPost).on(post.id.eq(groupBuyingPost.id))
                .innerJoin(member).on(post.writer.id.eq(member.id))
                .where(member.id.eq(memberId))
                .fetch()
                .size();

        return new PageImpl<>(result, pageable, totalCount);
    }

    public GroupBuyingPostDto findPostDetailById(Long postId, Long memberId) {
        memberId = memberId == null ? -1 : memberId;

        GroupBuyingPostDto result = jpaQueryFactory
                .select(getQGroupBuyingPostDtoSelectStatement(memberId))
                .from(post)
                .innerJoin(groupBuyingPost).on(post.id.eq(groupBuyingPost.id))
                .leftJoin(file).on(post.id.eq(file.post.id))
                .fetchJoin()
                .where(post.id.eq(postId))
                .fetchOne();

        if (result == null) {
            return null;
        }

        List<AttachedFileResponse> attachedFiles = jpaQueryFactory
                .select(new QAttachedFileResponse(file.id, file.filePath.prepend(s3Domain)).skipNulls())
                .from(file)
                .where(file.post.id.eq(postId))
                .fetch();

        if (!attachedFiles.isEmpty()) {
            result.setAttachedFiles(attachedFiles);
        }

        return result;
    }

    private QGroupBuyingPostDto getQGroupBuyingPostDtoSelectStatement(Long memberId) {
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
                groupBuyingPost.goodsPrice,
                groupBuyingPost.category,
                groupBuyingPost.groupBuyingStatus,
                groupBuyingPost.chatRoomLink,
                groupBuyingPost.deadline
        );
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
