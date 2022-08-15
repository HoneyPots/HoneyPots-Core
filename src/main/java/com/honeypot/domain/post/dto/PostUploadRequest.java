package com.honeypot.domain.post.dto;

import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.common.validation.groups.NormalPostUploadContext;
import com.honeypot.common.validation.groups.UsedTradePostUploadContext;
import com.honeypot.domain.file.PostFileUploadRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class PostUploadRequest {

    @Length(max = 30)
    @NotBlank
    private String title;

    @NotBlank(groups = {NormalPostUploadContext.class, UsedTradePostUploadContext.class})
    @Length(max = 1000)
    private String content;

    @NotNull(groups = InsertContext.class)
    private Long writerId;

    List<PostFileUploadRequest> attachedFiles;

}
