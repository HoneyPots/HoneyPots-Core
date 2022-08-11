package com.honeypot.domain.post.entity;

import com.honeypot.domain.post.entity.enums.GroupBuyingStatus;
import com.honeypot.domain.post.entity.enums.PostType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@DiscriminatorValue(PostType.Constant.GROUP_BUYING)
@DynamicUpdate
public class GroupBuyingPost extends Post {

    @Column(name = "goods_price")
    private int goodsPrice;

    @Column(name = "category")
    private String category;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "chat_room_link")
    private String chatRoomLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_buying_status")
    private GroupBuyingStatus groupBuyingStatus;

}
