package com.honeypot.domain.admin.repository;

import com.honeypot.domain.admin.dto.JoinMemberKpiResponse;
import com.honeypot.domain.admin.dto.QJoinMemberKpiResponse;
import com.honeypot.domain.admin.dto.UserEngagementKpiResponse;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.honeypot.domain.comment.entity.QComment.comment;
import static com.honeypot.domain.member.entity.QMember.member;
import static com.honeypot.domain.post.entity.QPost.post;
import static com.honeypot.domain.reaction.entity.QReaction.reaction;

@Repository
@RequiredArgsConstructor
public class AdminKpiRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<JoinMemberKpiResponse> getJoinMemberCountKpi(LocalDate fromDate, LocalDate toDate) {
        return jpaQueryFactory
                .select(new QJoinMemberKpiResponse(
                        member.createdAt.stringValue().substring(0, 10),
                        member.id.count().as("joinMemberCount")
                ))
                .from(member)
                .where(member.createdAt.between(fromDate.atStartOfDay(),
                        LocalDateTime.of(toDate, LocalTime.MAX).withNano(0)))
                .groupBy(member.createdAt.stringValue().substring(0, 10))
                .fetch();
    }

    public UserEngagementKpiResponse getUserEngagementKpi(LocalDate fromDate, LocalDate toDate) {
        long totalMemberCount = jpaQueryFactory
                .select(member.id.count())
                .from(member)
                .where(member.createdAt.between(fromDate.atStartOfDay(),
                                LocalDateTime.of(toDate, LocalTime.MAX).withNano(0))
                        .and(member.isWithdrawal.isFalse()))
                .fetchOne();

        long totalPostCount = jpaQueryFactory
                .select(post.id.count())
                .from(post)
                .where(post.createdAt.between(fromDate.atStartOfDay(),
                        LocalDateTime.of(toDate, LocalTime.MAX).withNano(0)))
                .fetchOne();

        long totalCommentCount = jpaQueryFactory
                .select(comment.id.count())
                .from(comment)
                .where(comment.createdAt.between(fromDate.atStartOfDay(),
                        LocalDateTime.of(toDate, LocalTime.MAX).withNano(0)))
                .fetchOne();

        long totalLikeReactionCount = jpaQueryFactory
                .select(reaction.id.count())
                .from(reaction)
                .where(reaction.reactionType.eq(ReactionType.LIKE))
                .fetchOne();

        return UserEngagementKpiResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalMemberCount(totalMemberCount)
                .totalPostCount(totalPostCount)
                .totalCommentCount(totalCommentCount)
                .totalLikeReactionCount(totalLikeReactionCount)
                .build();
    }

}
