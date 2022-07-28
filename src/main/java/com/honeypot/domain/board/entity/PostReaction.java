package com.honeypot.domain.board.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@DiscriminatorValue("POST")
public class PostReaction extends Reaction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

}
