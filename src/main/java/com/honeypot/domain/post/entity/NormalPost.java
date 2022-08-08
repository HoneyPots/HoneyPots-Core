package com.honeypot.domain.post.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("NORMAL")
@SuperBuilder
@NoArgsConstructor
@DynamicUpdate
public class NormalPost extends Post {

}
