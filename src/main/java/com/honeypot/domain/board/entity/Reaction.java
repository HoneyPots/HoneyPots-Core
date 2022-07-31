package com.honeypot.domain.board.entity;

import com.honeypot.domain.board.enums.ReactionTarget;
import com.honeypot.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "reaction_post_unique_key",
                        columnNames = {"member_id", "post_id", "reaction_type"}
                ),
                @UniqueConstraint(
                        name = "reaction_comment_unique_key",
                        columnNames = {"member_id", "comment_id", "reaction_type"}
                )
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "target_type", length = 10)
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member reactor;

    @Column(name = "reaction_type", nullable = false)
    private String reactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", insertable = false, updatable = false)
    private ReactionTarget targetType;

}
