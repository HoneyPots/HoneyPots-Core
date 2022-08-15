package com.honeypot.domain.member.repository;

import com.honeypot.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Override
    @Query(value = "SELECT * FROM member m WHERE m.member_id = :memberId AND is_withdrawal = false", nativeQuery = true)
    Optional<Member> findById(Long memberId);

    @Query(value = "SELECT CASE WHEN COUNT(*) >= 1 THEN false ELSE true END " +
            "FROM member m " +
            "WHERE m.nickname = :nickname " +
            "AND m.is_withdrawal = false", nativeQuery = true)
    boolean isAvailableNickname(String nickname);

    @Modifying
    @Query(value = "UPDATE member m " +
            "SET " +
            "  nickname = null, " +
            "  email = null, " +
            "  age_range = null, " +
            "  birthday = null, " +
            "  gender = null, " +
            "  is_withdrawal = true " +
            "WHERE m.member_id = :memberId",
            nativeQuery = true)
    int withdrawById(Long memberId);

}
