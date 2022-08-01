package com.honeypot.domain.post.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.validation.constraints.AllowedSortProperties;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.post.dto.NormalPostDto;
import com.honeypot.domain.post.service.NormalPostService;
import com.honeypot.domain.post.dto.NormalPostUploadRequest;
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

    private final AuthTokenManagerService authTokenManagerService;

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
    public ResponseEntity<?> upload(@RequestHeader("Authorization") String token,
                                    @Valid @RequestBody NormalPostUploadRequest uploadRequest) {

        if (authTokenManagerService.validate(token)) {
            throw new InvalidTokenException();
        }

        long memberId = authTokenManagerService.getMemberId(token);
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
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token,
                                    @PathVariable long postId,
                                    @Valid @RequestBody NormalPostUploadRequest uploadRequest) {

        if (authTokenManagerService.validate(token)) {
            throw new InvalidTokenException();
        }

        long memberId = authTokenManagerService.getMemberId(token);
        uploadRequest.setWriterId(memberId);

        NormalPostDto uploadedPost = normalPostService.update(postId, uploadRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token,
                                    @PathVariable long postId) {

        if (authTokenManagerService.validate(token)) {
            throw new InvalidTokenException();
        }

        long memberId = authTokenManagerService.getMemberId(token);

        normalPostService.delete(postId, memberId);

        return ResponseEntity
                .noContent()
                .build();
    }

}
