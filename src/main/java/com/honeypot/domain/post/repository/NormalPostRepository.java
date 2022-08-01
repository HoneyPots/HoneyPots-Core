package com.honeypot.domain.post.repository;

import com.honeypot.domain.post.entity.NormalPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalPostRepository extends JpaRepository<NormalPost, Long> {
}
