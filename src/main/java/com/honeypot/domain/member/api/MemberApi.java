package com.honeypot.domain.member.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.member.dto.NicknameModifyRequest;
import com.honeypot.domain.member.service.MemberNicknameModifyService;
import com.honeypot.domain.member.service.MemberWithdrawService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Validated
public class MemberApi {

    private final MemberNicknameModifyService memberNicknameModifyService;

    private final MemberWithdrawService memberWithdrawService;

    private final ObjectMapper objectMapper;

    @Value("${domain.server.domain-name}")
    private String serverDomainName;

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getNicknameProfile(@RequestParam @NotBlank @Length(max = 20) String nickname)
            throws JsonProcessingException {

        if (!memberNicknameModifyService.isAvailableNickname(nickname)) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("nickname", nickname);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(objectMapper.writeValueAsString(responseBody));
        }

        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{memberId}/profile/nickname")
    public ResponseEntity<?> changeNickname(@PathVariable Long memberId,
                                            @Valid @RequestBody NicknameModifyRequest request) throws JsonProcessingException {
        Long currentMemberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        if (!currentMemberId.equals(memberId)) {
            throw new InvalidAuthorizationException();
        }

        request.setMemberId(memberId);

        boolean isSucceed = memberNicknameModifyService.changeNickname(request);
        if (!isSucceed) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("nickname", request.getNickname());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(objectMapper.writeValueAsString(responseBody));
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long memberId,
                                          @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        Long currentMemberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        if (!currentMemberId.equals(memberId)) {
            throw new InvalidAuthorizationException();
        }

        boolean isSucceed = memberWithdrawService.withdraw(memberId);
        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, getHttpOnlyCookie(refreshToken).toString())
                .build();
    }

    private ResponseCookie getHttpOnlyCookie(String refreshToken) {
        return ResponseCookie
                .from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(serverDomainName)
                .maxAge(0)
                .build();
    }

}
