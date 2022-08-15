package com.honeypot.domain.post.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.common.validation.constraints.AllowedSortProperties;
import com.honeypot.common.validation.groups.UsedTradePostUploadContext;
import com.honeypot.domain.post.dto.UsedTradeModifyRequest;
import com.honeypot.domain.post.dto.UsedTradePostDto;
import com.honeypot.domain.post.dto.UsedTradePostUploadRequest;
import com.honeypot.domain.post.service.UsedTradePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/posts/used-trades")
@RequiredArgsConstructor
@Validated
public class UsedTradePostApi {

    private final UsedTradePostService usedTradePostService;

    @GetMapping
    public ResponseEntity<?> pageList(@AllowedSortProperties("createdAt") Pageable pageable) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElse(null);
        return ResponseEntity.ok(usedTradePostService.pageList(pageable, memberId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> read(@PathVariable long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElse(null);
        return ResponseEntity.ok(usedTradePostService.find(postId, memberId));
    }

    @PostMapping
    @Validated(UsedTradePostUploadContext.class)
    public ResponseEntity<?> upload(@Valid @RequestBody UsedTradePostUploadRequest uploadRequest) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        uploadRequest.setWriterId(memberId);

        UsedTradePostDto uploadedPost = usedTradePostService.upload(uploadRequest);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{postId}")
                        .buildAndExpand(uploadedPost.getPostId())
                        .toUri())
                .body(uploadedPost);
    }

    @PutMapping("/{postId}")
    @Validated(UsedTradePostUploadContext.class)
    public ResponseEntity<?> update(@PathVariable long postId,
                                    @Valid @RequestBody UsedTradePostUploadRequest uploadRequest) {

        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        uploadRequest.setWriterId(memberId);
        UsedTradePostDto uploadedPost = usedTradePostService.update(postId, uploadRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<?> updateTradeStatus(@PathVariable long postId,
                                               @Valid @RequestBody UsedTradeModifyRequest request) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        request.setWriterId(memberId);
        UsedTradePostDto updated = usedTradePostService.updateTradeStatus(postId, request);

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(@PathVariable long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        usedTradePostService.delete(postId, memberId);

        return ResponseEntity
                .noContent()
                .build();
    }

}
