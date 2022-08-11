package com.honeypot.domain.auth.repository;

import com.honeypot.domain.auth.entity.AuthProvider;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {

    Optional<AuthProvider> findByProviderTypeAndProviderMemberId(AuthProviderType providerType, String providerMemberId);

    @Modifying
    @Query(value = "DELETE FROM auth_provider ap WHERE ap.member_id = :memberId", nativeQuery = true)
    int deleteByMemberId(Long memberId);

}
