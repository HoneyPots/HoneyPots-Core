package com.honeypot.domain.search;

import com.honeypot.domain.post.dto.PostDto;
import com.honeypot.domain.post.repository.PostQuerydslSelectStatementFactory;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
import static com.honeypot.domain.post.entity.QGroupBuyingPost.groupBuyingPost;
import static com.honeypot.domain.post.entity.QNormalPost.normalPost;
import static com.honeypot.domain.post.entity.QPost.post;
import static com.honeypot.domain.post.entity.QUsedTradePost.usedTradePost;

@Repository
@RequiredArgsConstructor
public class PostSearchQuerydslRepository {

    @Value("${cloud.aws.s3.domain}")
    private String s3Domain;

    private final JPAQueryFactory jpaQueryFactory;

    public Page<? extends PostDto> findAllPost(PostSearchCriteria criteria, Pageable pageable, Long memberId) {
        memberId = memberId == null ? -1 : memberId;

        List<? extends PostDto> result = jpaQueryFactory
                .select(PostQuerydslSelectStatementFactory.of(criteria.getPostType(), memberId, s3Domain))
                .from(post)
                .leftJoin(normalPost).on(post.id.eq(normalPost.id))
                .leftJoin(usedTradePost).on(post.id.eq(usedTradePost.id))
                .leftJoin(groupBuyingPost).on(post.id.eq(groupBuyingPost.id))
                .leftJoin(file).on(post.id.eq(file.post.id))
                .fetchJoin()
                .where(post.type.eq(criteria.getPostType().name())
                        .and(post.title.containsIgnoreCase(criteria.getKeyword())
                                .or(post.content.containsIgnoreCase(criteria.getKeyword()))
                        )
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sort(pageable.getSort()))
                .groupBy(post.id)
                .fetch();

        long totalCount = jpaQueryFactory
                .select(post.id)
                .from(post)
                .where(post.type.eq(criteria.getPostType().name())
                        .and(post.title.containsIgnoreCase(criteria.getKeyword())
                                .or(post.content.containsIgnoreCase(criteria.getKeyword()))
                        )
                )
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
