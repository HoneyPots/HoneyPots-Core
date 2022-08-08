package com.honeypot.domain.post.entity;

import com.honeypot.domain.post.entity.enums.TradeStatus;
import com.honeypot.domain.post.entity.enums.TradeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@DiscriminatorValue("USED_TRADE")
public class UsedTradePost extends Post {

    @Column(name = "goods_price")
    private int goodsPrice;

    @Column(name = "chat_room_link")
    private String chatRoomLink;

    @Column(name = "trade_type")
    private TradeType tradeType;

    @Column(name = "trade_status")
    private TradeStatus tradeStatus;

}
