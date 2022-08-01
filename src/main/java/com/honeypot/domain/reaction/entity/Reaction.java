package com.honeypot.domain.reaction.entity;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.reaction.entity.enums.ReactionTarget;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
    @ToString.Exclude
    private Member reactor;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", insertable = false, updatable = false)
    private ReactionTarget targetType;

    @Column(name = "post_id", insertable = false, updatable = false)
    private Long postId;

    @Column(name = "comment_id", insertable = false, updatable = false)
    private Long commentId;

}
