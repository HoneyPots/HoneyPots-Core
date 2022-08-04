package com.honeypot.domain.comment.repository;

import com.honeypot.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    long countByPostId(Long postId);

}
