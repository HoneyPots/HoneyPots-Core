package com.honeypot.domain.search;

import com.honeypot.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    private final SearchHistoryMapper searchHistoryMapper;

    public SearchHistoryDto save(String keyword, SearchType searchType, Long memberId) {
        SearchHistory entity = SearchHistory.builder()
                .keyword(keyword)
                .type(searchType)
                .searcher(Member.builder().id(memberId).build())
                .build();

        SearchHistory created = searchHistoryRepository.save(entity);
        return searchHistoryMapper.toDto(created);
    }

}

