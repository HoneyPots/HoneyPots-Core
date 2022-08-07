package com.honeypot.domain.file;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public enum FileType {

    NORMAL_POST_IMAGE("img/normal-posts/", List.of("jpg", "jpeg", "png")),

    USED_TRADE_POST_IMAGE("img/used-trades/", List.of("jpg", "jpeg", "png"));

    public final String fileDirectory;

    public final List<String> allowedExtension;

}
