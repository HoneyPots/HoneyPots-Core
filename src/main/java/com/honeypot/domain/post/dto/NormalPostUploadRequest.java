package com.honeypot.domain.post.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
public class NormalPostUploadRequest extends PostUploadRequest {

}
