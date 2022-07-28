package com.honeypot.domain.board.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Entity
@DiscriminatorValue("NORMAL")
@SuperBuilder
@NoArgsConstructor
public class NormalPost extends Post {

}
