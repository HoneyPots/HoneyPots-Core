package com.honeypot.domain.board.api;

import com.honeypot.common.validation.constraints.AllowedSortProperties;
import com.honeypot.domain.board.dto.CommentDto;
import com.honeypot.domain.board.dto.CommentUploadRequest;
import com.honeypot.domain.board.dto.NormalPostDto;
import com.honeypot.domain.board.dto.NormalPostUploadRequest;
import com.honeypot.domain.board.service.CommentService;
import com.honeypot.domain.board.service.NormalPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/post/{postId}/comment")
@RequiredArgsConstructor
@Validated
public class CommentApi {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<?> pageList(@PathVariable long postId,
                                      @AllowedSortProperties("createdAt") Pageable pageable) {
        return ResponseEntity.ok(commentService.pageList(postId, pageable));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> read(@PathVariable long postId,
                                  @PathVariable long commentId) {
        return ResponseEntity.ok(commentService.find(postId, commentId));
    }

    @PostMapping
    public ResponseEntity<?> write(@RequestHeader("Authorization") String token,
                                   @PathVariable long postId,
                                   @Valid @RequestBody CommentUploadRequest uploadRequest) {

        // TODO validate token and find member id
        long writerId = 1;

        uploadRequest.setPostId(postId);
        uploadRequest.setWriterId(writerId);

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
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token,
                                    @PathVariable long postId,
                                    @PathVariable long commentId,
                                    @Valid @RequestBody CommentUploadRequest uploadRequest) {

        // TODO validate token and find member id
        long writerId = 1;
        uploadRequest.setPostId(postId);
        uploadRequest.setWriterId(writerId);

        CommentDto updatedComment = commentService.update(commentId, uploadRequest);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token,
                                    @PathVariable long postId,
                                    @PathVariable long commentId) {

        // TODO validate token and find member id
        long memberId = 1L;
        commentService.delete(commentId, memberId);

        return ResponseEntity
                .noContent()
                .build();
    }

}
