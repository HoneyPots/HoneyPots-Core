package com.honeypot.domain.post.dto;

import com.honeypot.common.validation.constraints.Enum;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.post.entity.enums.TradeStatus;
import com.honeypot.domain.post.entity.enums.TradeType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UsedTradePostUploadRequest extends NormalPostUploadRequest {

    @PositiveOrZero
    private int goodsPrice;

    @Enum(target = TradeType.class, groups = InsertContext.class)
    private String tradeType;

    @Enum(target = TradeStatus.class, ifNull = true, groups = InsertContext.class)
    private String tradeStatus;

    private String chatRoomLink;

}
