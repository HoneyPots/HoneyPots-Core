package com.honeypot.domain.search;

import com.honeypot.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchHistoryMapperTest {

    private final SearchHistoryMapper mapper = Mappers.getMapper(SearchHistoryMapper.class);

    @Test
    void toEntity() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        SearchHistoryDto dto = SearchHistoryDto.builder()
                .searchHistoryId(1L)
                .keyword("꿀단지")
                .type(SearchType.POST)
                .searcherId(12L)
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        // Act
        SearchHistory entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getSearchHistoryId(), entity.getId());
        assertEquals(dto.getKeyword(), entity.getKeyword());
        assertEquals(dto.getType(), entity.getType());
        assertEquals(dto.getSearcherId(), entity.getSearcher().getId());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toDto() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        SearchHistory entity = SearchHistory.builder()
                .id(1L)
                .keyword("꿀단지")
                .type(SearchType.POST)
                .searcher(Member.builder().id(1L).nickname("nick").build())
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        // Act
        SearchHistoryDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getSearchHistoryId());
        assertEquals(entity.getKeyword(), dto.getKeyword());
        assertEquals(entity.getType(), dto.getType());
        assertEquals(entity.getSearcher().getId(), dto.getSearcherId());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getLastModifiedAt());
    }

}