package com.honeypot.domain.search;

import com.honeypot.domain.post.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Validated
public class PostSearchService {

    private final PostSearchQuerydslRepository postSearchQuerydslRepository;

    private final SearchHistoryService searchHistoryService;

    @Transactional(readOnly = true)
    public Page<? extends PostDto> search(@Valid PostSearchCriteria criteria,
                                          Pageable pageable,
                                          Long memberId) {
        Page<? extends PostDto> result = postSearchQuerydslRepository.findAllPost(criteria, pageable, memberId);
        searchHistoryService.save(criteria.getKeyword(), SearchType.POST, memberId);
        return new PageImpl<>(
                result.getContent(),
                pageable,
                result.getTotalElements()
        );
    }

}
