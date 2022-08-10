package com.honeypot.domain.member.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.member.dto.NicknameModifyRequest;
import com.honeypot.domain.member.service.MemberNicknameModifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Validated
public class MemberApi {

    private final MemberNicknameModifyService memberNicknameModifyService;

    private final ObjectMapper objectMapper;

    @PatchMapping("/nickname")
    public ResponseEntity<?> changeNickname(@Valid @RequestBody NicknameModifyRequest request) throws JsonProcessingException {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
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

}
