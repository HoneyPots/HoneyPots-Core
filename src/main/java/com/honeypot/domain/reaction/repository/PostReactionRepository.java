package com.honeypot.domain.reaction.repository;

import com.honeypot.domain.reaction.entity.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

}
