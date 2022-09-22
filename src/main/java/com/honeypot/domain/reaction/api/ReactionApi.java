package com.honeypot.domain.reaction.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.reaction.dto.ReactionDto;
import com.honeypot.domain.reaction.dto.ReactionRequest;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import com.honeypot.domain.reaction.service.PostReactionService;
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

    private final PostReactionService postReactionService;

    @GetMapping("/likes/{reactionId}")
    public ResponseEntity<?> getLikeReaction(@PathVariable Long reactionId) {
        return ResponseEntity.ok(postReactionService.find(reactionId));
    }

    @PostMapping("/likes")
    public ResponseEntity<?> reactLike(@Valid @RequestBody ReactionRequest reactionRequest) {

        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        reactionRequest.setReactorId(memberId);
        reactionRequest.setReactionType(ReactionType.LIKE);

        ReactionDto result = postReactionService.save(reactionRequest);

        if (result.isAlreadyExists()) {
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

    @DeleteMapping("/likes/{reactionId}")
    public ResponseEntity<?> cancelLikeReaction(@PathVariable Long reactionId) {

        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        postReactionService.cancel(memberId, reactionId);

        return ResponseEntity.noContent().build();
    }

}
