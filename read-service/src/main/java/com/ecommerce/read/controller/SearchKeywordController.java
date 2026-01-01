package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.SearchKeywordDTO;
import com.ecommerce.read.entity.SearchKeyword;
import com.ecommerce.read.service.SearchKeywordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = Constant.SEARCH_KEYWORD)
@RequiredArgsConstructor
public class SearchKeywordController {

    private final SearchKeywordService searchKeywordService;
    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Search products by keyword", description = "Retrieve products matching the given keyword")
    public ResponseEntity<BaseResponse<Void>> searchProductsByKeyword(@RequestBody SearchKeywordDTO searchKeywordDTO) {
        searchKeywordService.createSearchKeyword(searchKeywordDTO);
        return ResponseEntity.ok(
            BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.SEARCH_KEYWORD_CREATED_SUCCESS))
                .build()
        );
    }

    @GetMapping
    @Operation(summary = "Get popular search keywords", description = "Retrieve a list of popular search keywords")
    public ResponseEntity<BaseResponse<PageResponse<SearchKeyword>>> getPopularSearchKeywords(
        @RequestParam() String keyword,
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        PageResponse<SearchKeyword> keywords = searchKeywordService.getPopularSearchKeywords(keyword, pageNo, pageSize);
        return ResponseEntity.ok(
            BaseResponse.<PageResponse<SearchKeyword>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.SEARCH_KEYWORDS_RETRIEVED_SUCCESS))
                .data(keywords)
                .build()
        );
    }
}
