package com.honeypot.domain.board.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@DiscriminatorValue("Normal")
public class NormalPost extends Post {

}
