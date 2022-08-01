package com.honeypot.domain.member.entity;

import com.honeypot.common.entity.BaseTimeEntity;
import com.honeypot.domain.member.entity.enums.Gender;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "nickname", nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "age_range")
    private String ageRange;

    @Column(name = "birthday", length = 4)
    private String birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

}
