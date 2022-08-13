package com.honeypot.domain.post.dto;

import com.honeypot.common.validation.constraints.Enum;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.post.entity.enums.GroupBuyingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyingPostUploadRequest extends PostUploadRequest {

    @NotEmpty
    private String category;

    @Enum(target = GroupBuyingStatus.class, ifNull = true, groups = InsertContext.class)
    private String groupBuyingStatus;

    private String chatRoomLink;

    @FutureOrPresent
    private LocalDateTime deadline;

}
