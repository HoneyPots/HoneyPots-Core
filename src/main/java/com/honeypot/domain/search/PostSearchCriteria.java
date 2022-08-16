package com.honeypot.domain.search;

import com.honeypot.domain.post.entity.enums.PostType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class PostSearchCriteria {

    @NotNull
    @Length(min = 1, max = 30)
    private String keyword;

    @NotNull
    private PostType postType;

}
