package com.honeypot.domain.post.dto;

import com.honeypot.common.validation.constraints.Enum;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.post.entity.enums.GroupBuyingStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class GroupBuyingModifyRequest {

    @Enum(target = GroupBuyingStatus.class)
    private String groupBuyingStatus;

    @NotNull(groups = InsertContext.class)
    private Long writerId;

}
