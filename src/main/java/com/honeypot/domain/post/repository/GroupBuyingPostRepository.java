package com.honeypot.domain.post.repository;

import com.honeypot.domain.post.entity.GroupBuyingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBuyingPostRepository extends JpaRepository<GroupBuyingPost, Long> {
}
