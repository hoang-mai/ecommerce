package com.ecommerce.read.dto;

import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.ProductView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewHomePageDTO {
    private List<String> showProductIds;
    private PageResponse<ProductView> pageResponse;
}
