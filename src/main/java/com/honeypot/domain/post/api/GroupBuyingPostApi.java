package com.honeypot.domain.post.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.common.validation.constraints.AllowedSortProperties;
import com.honeypot.domain.post.dto.GroupBuyingModifyRequest;
import com.honeypot.domain.post.dto.GroupBuyingPostDto;
import com.honeypot.domain.post.dto.GroupBuyingPostUploadRequest;
import com.honeypot.domain.post.service.GroupBuyingPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/posts/group-buying")
@RequiredArgsConstructor
@Validated
public class GroupBuyingPostApi {

    private final GroupBuyingPostService groupBuyingPostService;

    @GetMapping
    public ResponseEntity<?> pageList(@AllowedSortProperties("createdAt") Pageable pageable) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElse(null);
        return ResponseEntity.ok(groupBuyingPostService.pageList(pageable, memberId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> read(@PathVariable long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElse(null);
        return ResponseEntity.ok(groupBuyingPostService.find(postId, memberId));
    }

    @PostMapping
    public ResponseEntity<?> upload(@Valid @RequestBody GroupBuyingPostUploadRequest uploadRequest) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        uploadRequest.setWriterId(memberId);

        GroupBuyingPostDto uploadedPost = groupBuyingPostService.upload(uploadRequest);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{postId}")
                        .buildAndExpand(uploadedPost.getPostId())
                        .toUri())
                .body(uploadedPost);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> update(@PathVariable long postId,
                                    @Valid @RequestBody GroupBuyingPostUploadRequest uploadRequest) {

        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        uploadRequest.setWriterId(memberId);
        GroupBuyingPostDto uploadedPost = groupBuyingPostService.update(postId, uploadRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<?> updateTradeStatus(@PathVariable long postId,
                                               @Valid @RequestBody GroupBuyingModifyRequest request) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        request.setWriterId(memberId);
        GroupBuyingPostDto updated = groupBuyingPostService.updateTradeStatus(postId, request);

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(@PathVariable long postId) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        groupBuyingPostService.delete(postId, memberId);

        return ResponseEntity
                .noContent()
                .build();
    }

}
