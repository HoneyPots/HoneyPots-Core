package com.honeypot.common.config;

import com.honeypot.common.filter.AuthenticationFilter;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] PUBLIC_APIS = {"/img/**", "/error"};

    private final AuthTokenManagerService jwtAuthTokenManagerService;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(PUBLIC_APIS);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()

                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())

                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .anyRequest().permitAll()

                .and()
                .addFilterBefore(new AuthenticationFilter(jwtAuthTokenManagerService),
                        UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException)
                -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException)
                -> response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
