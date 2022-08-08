package com.honeypot.domain.post.dto;

import com.honeypot.common.validation.constraints.Enum;
import com.honeypot.domain.post.entity.enums.TradeType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.PositiveOrZero;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UsedTradePostUploadRequest extends NormalPostUploadRequest {

    @PositiveOrZero
    private int goodsPrice;

    @Enum(target = TradeType.class)
    private String tradeType;

    private String chatRoomLink;

}
