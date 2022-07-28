package com.honeypot.domain.board.entity;

import com.honeypot.domain.member.entity.Member;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "reaction_unique_key",
                        columnNames = {"member_id", "target_type"}
                )
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "target_type", length = 10)
public abstract class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reaction_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "reaction_type", nullable = false)
    private String reactionType;

    @Column(name = "target_type", insertable = false, updatable = false)
    private String targetType;

}
