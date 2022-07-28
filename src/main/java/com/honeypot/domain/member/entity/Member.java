package com.honeypot.domain.member.entity;

import com.honeypot.common.entity.BaseTimeEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor
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

    @Column(name = "gender")
    private String gender;

}
