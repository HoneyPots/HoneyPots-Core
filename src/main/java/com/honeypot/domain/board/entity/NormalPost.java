package com.honeypot.domain.board.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@DiscriminatorValue("NORMAL")
public class NormalPost extends Post {

}
