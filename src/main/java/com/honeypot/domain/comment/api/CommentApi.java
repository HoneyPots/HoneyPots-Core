package com.honeypot.domain.comment.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.common.validation.constraints.AllowedSortProperties;
import com.honeypot.domain.comment.dto.CommentDto;
import com.honeypot.domain.comment.service.CommentService;
import com.honeypot.domain.comment.dto.CommentUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentApi {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<?> pageList(@RequestParam long postId,
                                      @AllowedSortProperties("createdAt") Pageable pageable) {
        return ResponseEntity.ok(commentService.pageList(postId, pageable));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> read(@RequestParam long postId,
                                  @PathVariable long commentId) {
        return ResponseEntity.ok(commentService.find(postId, commentId));
    }

    @PostMapping
    public ResponseEntity<?> write(@Valid @RequestBody CommentUploadRequest uploadRequest) {

        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        uploadRequest.setWriterId(memberId);

        CommentDto createdComment = commentService.save(uploadRequest);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{commentId}")
                        .buildAndExpand(createdComment.getCommentId())
                        .toUri())
                .body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> update(@PathVariable long commentId,
                                    @Valid @RequestBody CommentUploadRequest uploadRequest) {

        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        uploadRequest.setWriterId(memberId);

        CommentDto updatedComment = commentService.update(commentId, uploadRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> delete(@PathVariable long commentId) {

        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        commentService.delete(commentId, memberId);

        return ResponseEntity
                .noContent()
                .build();
    }

}
