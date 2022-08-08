package com.honeypot.domain.post.dto;

import com.honeypot.domain.post.entity.enums.TradeStatus;
import com.honeypot.domain.post.entity.enums.TradeType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class UsedTradePostDto extends NormalPostDto {

    @PositiveOrZero
    private int goodsPrice;

    @NotNull
    private TradeType tradeType;

    @NotNull
    private TradeStatus tradeStatus;

    private String chatRoomLink;

}
