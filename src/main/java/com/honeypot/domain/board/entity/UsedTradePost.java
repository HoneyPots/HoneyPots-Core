package com.honeypot.domain.board.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Entity
@DiscriminatorValue("USED_TRADE")
public class UsedTradePost extends Post {

    @Column(name = "goods_price")
    private int goodsPrice;

    @Column(name = "chat_room_link")
    private String chatRoomLink;

    @Column(name = "trade_type")
    private String tradeType;

    @Column(name = "trade_area")
    private String tradeArea;

    @Column(name = "trade_status")
    private String tradeStatus;

}
