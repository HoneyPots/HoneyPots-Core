package com.honeypot.domain.search;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SearchHistoryDto {

    private Long searchHistoryId;

    private String keyword;

    private SearchType type;

    private Long searcherId;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

}
