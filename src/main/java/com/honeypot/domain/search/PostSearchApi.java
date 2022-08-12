package com.honeypot.domain.search;

import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.common.validation.constraints.AllowedSortProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class PostSearchApi {

    private final PostSearchService postSearchService;

    @GetMapping("/posts")
    public ResponseEntity<?> search(@Valid PostSearchCriteria criteria,
                                    @AllowedSortProperties("createdAt") Pageable pageable) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElse(null);
        return ResponseEntity.ok(postSearchService.search(criteria, pageable, memberId));
    }

}
