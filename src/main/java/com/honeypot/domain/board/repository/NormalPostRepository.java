package com.honeypot.domain.board.repository;

import com.honeypot.domain.board.entity.NormalPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalPostRepository extends JpaRepository<NormalPost, Long> {
}
