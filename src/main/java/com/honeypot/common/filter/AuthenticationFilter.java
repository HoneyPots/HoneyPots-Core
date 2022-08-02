package com.honeypot.common.filter;

import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends GenericFilterBean {

    private static final String BEARER_TOKEN_SCHEME = "Bearer ";

    private final AuthTokenManagerService authTokenManagerService;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = resolveToken(httpServletRequest);
        String requestUri = httpServletRequest.getRequestURI();

        if (token != null && authTokenManagerService.validate(token)) {
            Long memberId = authTokenManagerService.getMemberId(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(memberId, token, new ArrayList<>());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authentication information '{}' saved in security context. uri: {}",
                    authentication.getName(), requestUri);
        } else {
            log.debug("There is no valid JWT. uri: {}", requestUri);
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith(BEARER_TOKEN_SCHEME)) {
            return token.substring(BEARER_TOKEN_SCHEME.length());
        }

        return null;
    }
}
