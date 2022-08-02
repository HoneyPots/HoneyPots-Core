package com.honeypot.domain.post.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.common.validation.constraints.AllowedSortProperties;
import com.honeypot.domain.post.dto.NormalPostDto;
import com.honeypot.domain.post.dto.NormalPostUploadRequest;
import com.honeypot.domain.post.service.NormalPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/posts/normal")
@RequiredArgsConstructor
@Validated
public class NormalPostApi {

    private final NormalPostService normalPostService;

    @GetMapping
    public ResponseEntity<?> pageList(@AllowedSortProperties("createdAt") Pageable pageable) {
        return ResponseEntity.ok(normalPostService.pageList(pageable));
    }

    @GetMapping("{postId}")
    public ResponseEntity<?> read(@PathVariable long postId) {
        return ResponseEntity.ok(normalPostService.find(postId));
    }

    @PostMapping
    public ResponseEntity<?> upload(@Valid @RequestBody NormalPostUploadRequest uploadRequest) {

        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        uploadRequest.setWriterId(memberId);

        NormalPostDto uploadedPost = normalPostService.upload(uploadRequest);

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
                                    @Valid @RequestBody NormalPostUploadRequest uploadRequest) {

        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        uploadRequest.setWriterId(memberId);
        NormalPostDto uploadedPost = normalPostService.update(postId, uploadRequest);

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(@PathVariable long postId) {

        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        normalPostService.delete(postId, memberId);

        return ResponseEntity
                .noContent()
                .build();
    }

}
