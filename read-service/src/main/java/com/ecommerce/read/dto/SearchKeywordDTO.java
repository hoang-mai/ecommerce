package com.ecommerce.read.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchKeywordDTO {
    private String keyword;
}
