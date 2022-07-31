package com.honeypot.domain.board.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.domain.auth.service.TokenManagerService;
import com.honeypot.domain.board.dto.ReactionDto;
import com.honeypot.domain.board.dto.ReactionRequest;
import com.honeypot.domain.board.enums.ReactionTarget;
import com.honeypot.domain.board.enums.ReactionType;
import com.honeypot.domain.board.service.CommentReactionService;
import com.honeypot.domain.board.service.PostReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
@Validated
public class ReactionApi {

    private final TokenManagerService tokenManagerService;

    private final PostReactionService postReactionService;

    private final CommentReactionService commentReactionService;

    @PostMapping("/likes")
    public ResponseEntity<?> react(@RequestHeader("Authorization") String token,
                                   @Valid @RequestBody ReactionRequest reactionRequest) {

        if (tokenManagerService.verifyToken(token)) {
            throw new InvalidTokenException();
        }

        long memberId = tokenManagerService.getUserId(token);
        reactionRequest.setReactorId(memberId);
        reactionRequest.setReactionType(ReactionType.LIKE);

        ReactionDto result;
        if (reactionRequest.getTargetType() == ReactionTarget.POST) {
            result = postReactionService.save(reactionRequest);
        } else if (reactionRequest.getTargetType() == ReactionTarget.COMMENT) {
            result = commentReactionService.save(reactionRequest);
        } else {
            return ResponseEntity.badRequest().build();
        }

        if(result.isAlreadyExists()) {
            return ResponseEntity.ok(result);
        }

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{reactionId}")
                        .buildAndExpand(result.getReactionId())
                        .toUri())
                .body(result);
    }

}
