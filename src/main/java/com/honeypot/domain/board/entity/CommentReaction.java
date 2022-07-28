package com.honeypot.domain.board.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@DiscriminatorValue("COMMENT")
public class CommentReaction extends Reaction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

}
