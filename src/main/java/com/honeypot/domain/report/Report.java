package com.honeypot.domain.report;

import com.honeypot.common.entity.BaseTimeEntity;
import com.honeypot.domain.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target", nullable = false)
    private ReportTarget target;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "reason")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member reporter;

}
