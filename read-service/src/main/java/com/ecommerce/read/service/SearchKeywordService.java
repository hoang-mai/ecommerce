package com.ecommerce.read.service;

import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.SearchKeywordDTO;
import com.ecommerce.read.entity.SearchKeyword;

public interface SearchKeywordService {
    void createSearchKeyword(SearchKeywordDTO searchKeywordDTO);

    PageResponse<SearchKeyword> getPopularSearchKeywords(String keyword,int pageNo, int pageSize);
}
