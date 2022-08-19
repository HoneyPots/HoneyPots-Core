package com.honeypot.domain.post.dto;

import com.honeypot.common.validation.constraints.Enum;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.post.entity.enums.TradeStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
public class UsedTradeModifyRequest {

    @Enum(target = TradeStatus.class)
    private String tradeStatus;

    @NotNull(groups = InsertContext.class)
    private Long writerId;

}
