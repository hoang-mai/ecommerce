package com.ecommerce.read.service.impl;

import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.SearchKeywordDTO;
import com.ecommerce.read.entity.SearchKeyword;
import com.ecommerce.read.repository.SearchKeywordRepository;
import com.ecommerce.read.service.SearchKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SearchKeywordServiceImpl implements SearchKeywordService {
    private final SearchKeywordRepository searchKeywordRepository;
    @Override
    public void createSearchKeyword(SearchKeywordDTO searchKeywordDTO) {
        SearchKeyword searchKeyword = searchKeywordRepository.findByKeyword(searchKeywordDTO.getKeyword())
                .orElse(SearchKeyword.builder()
                        .keyword(searchKeywordDTO.getKeyword())
                        .searchCount(0L)
                        .build());
        searchKeyword.setSearchCount(searchKeyword.getSearchCount() + 1);
        searchKeyword.setLastSearchedAt(Instant.now());
        searchKeywordRepository.save(searchKeyword);
    }

    @Override
    public PageResponse<SearchKeyword> getPopularSearchKeywords(String keyword,int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<SearchKeyword> keywordPage = searchKeywordRepository.getPopularSearchKeywords(keyword, pageable);
        return PageResponse.<SearchKeyword>builder()
                .data(keywordPage.getContent())
                .pageNo(keywordPage.getNumber())
                .pageSize(keywordPage.getSize())
                .totalElements(keywordPage.getTotalElements())
                .totalPages(keywordPage.getTotalPages())
                .hasNextPage(keywordPage.hasNext())
                .hasPreviousPage(keywordPage.hasPrevious())
                .build();
    }
}
