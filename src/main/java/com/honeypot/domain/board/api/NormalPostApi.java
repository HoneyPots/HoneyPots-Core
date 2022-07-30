package com.honeypot.domain.board.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.validation.constraints.AllowedSortProperties;
import com.honeypot.domain.auth.service.TokenManagerService;
import com.honeypot.domain.board.dto.NormalPostDto;
import com.honeypot.domain.board.dto.NormalPostUploadRequest;
import com.honeypot.domain.board.service.NormalPostService;
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

    private final TokenManagerService tokenManagerService;

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

        if (tokenManagerService.verifyToken(token)) {
            throw new InvalidTokenException();
        }

        long memberId = tokenManagerService.getUserId(token);
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

        if (tokenManagerService.verifyToken(token)) {
            throw new InvalidTokenException();
        }

        long memberId = tokenManagerService.getUserId(token);
        uploadRequest.setWriterId(memberId);

        NormalPostDto uploadedPost = normalPostService.update(postId, uploadRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token,
                                    @PathVariable long postId) {

        if (tokenManagerService.verifyToken(token)) {
            throw new InvalidTokenException();
        }

        long memberId = tokenManagerService.getUserId(token);

        normalPostService.delete(postId, memberId);

        return ResponseEntity
                .noContent()
                .build();
    }

}
