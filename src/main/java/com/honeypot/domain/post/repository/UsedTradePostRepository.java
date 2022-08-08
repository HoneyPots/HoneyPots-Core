package com.honeypot.domain.post.repository;

import com.honeypot.domain.post.entity.UsedTradePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsedTradePostRepository extends JpaRepository<UsedTradePost, Long> {
}
