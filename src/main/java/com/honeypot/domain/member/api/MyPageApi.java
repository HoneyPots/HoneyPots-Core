package com.honeypot.domain.member.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.common.validation.constraints.AllowedSortProperties;
import com.honeypot.domain.post.entity.enums.PostType;
import com.honeypot.domain.post.service.PostCrudServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Validated
public class MyPageApi {

    private final PostCrudServiceFactory postCrudServiceFactory;

    @GetMapping("/posts")
    public ResponseEntity<?> pageList(@RequestParam PostType postType,
                                      @AllowedSortProperties("createdAt") Pageable pageable) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        return ResponseEntity.ok(postCrudServiceFactory
                .getService(postType)
                .pageListByMemberId(pageable, memberId));
    }

}
