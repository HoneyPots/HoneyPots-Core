package com.honeypot.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class SecurityUtils {

    private SecurityUtils() {
        // no-op
    }

    public static Optional<Long> getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("There in no authentication information in Security context");
            return Optional.empty();
        }

        Long memberId = null;
        if (authentication.getPrincipal() instanceof Long) {
            memberId = (Long) authentication.getPrincipal();
        }

        return Optional.ofNullable(memberId);
    }
}
