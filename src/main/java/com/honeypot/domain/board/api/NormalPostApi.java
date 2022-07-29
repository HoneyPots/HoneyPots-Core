package com.honeypot.domain.board.api;

import com.honeypot.domain.board.dto.NormalPostDto;
import com.honeypot.domain.board.dto.NormalPostUploadRequest;
import com.honeypot.domain.board.service.NormalPostUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/post/normal")
@RequiredArgsConstructor
public class NormalPostApi {

    private final NormalPostUploadService normalPostUploadService;

    @GetMapping
    public ResponseEntity<?> pageList(Pageable pageable) {
        return ResponseEntity.ok(normalPostUploadService.pageList(pageable));
    }

    @PostMapping
    public ResponseEntity<?> upload(@RequestHeader("Authorization") String token,
                                    @Valid @RequestBody NormalPostUploadRequest uploadRequest) {


        // TODO validate token and find member id
        long writerId = 1;
        uploadRequest.setWriterId(writerId);

        NormalPostDto uploadedPost = normalPostUploadService.upload(uploadRequest);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{postId}")
                        .buildAndExpand(uploadedPost.getPostId())
                        .toUri())
                .body(uploadedPost);
    }

}
