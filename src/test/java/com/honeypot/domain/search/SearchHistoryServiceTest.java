package com.honeypot.domain.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class SearchHistoryServiceTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private SearchHistoryMapper searchHistoryMapper;

    private SearchHistoryService searchHistoryService;

    @BeforeEach
    private void before() {
        this.searchHistoryService = new SearchHistoryService(searchHistoryRepository, searchHistoryMapper);
    }


    @Test
    void save() {
        // Arrange
        String keyword = "꿀단지";
        SearchType type = SearchType.POST;
        Long memberId = 1L;

        SearchHistoryDto expected = SearchHistoryDto.builder()
                .searchHistoryId(1L)
                .keyword(keyword)
                .type(type)
                .searcherId(memberId)
                .build();

        when(searchHistoryService.save(keyword, type, memberId)).thenReturn(expected);

        // Act
        SearchHistoryDto result = searchHistoryService.save(keyword, type, memberId);

        // Assert
        assertEquals(expected.getKeyword(), result.getKeyword());
        assertEquals(expected.getType(), result.getType());
        assertEquals(expected.getSearchHistoryId(), result.getSearcherId());
    }

}