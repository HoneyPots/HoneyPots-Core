package com.honeypot.domain.member.repository;

import com.honeypot.domain.member.entity.AuthProvider;
import com.honeypot.domain.member.enums.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {

    AuthProvider findByProviderTypeAndProviderMemberId(AuthProviderType providerType, long providerMemberId);
}