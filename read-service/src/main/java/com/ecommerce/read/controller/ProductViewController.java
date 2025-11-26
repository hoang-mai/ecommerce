package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.ProductViewDTO;
import com.ecommerce.read.entity.ProductView;
import com.ecommerce.read.service.ProductViewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = Constant.PRODUCT_VIEW)
@RequiredArgsConstructor
public class ProductViewController {
    private final ProductViewService productViewService;
    private final MessageService messageService;

    /**
     * Tìm kiếm sản phẩm với nhiều bộ lọc
     *
     * @param shopId     ID của shop (optional)
     * @param categoryId ID của category (optional)
     * @param status     Trạng thái sản phẩm (optional)
     * @param keyword    Từ khóa tìm kiếm (optional)
     * @param pageNo     Số trang (mặc định là 0)
     * @param pageSize   Kích thước trang (mặc định là 10)
     * @param sortBy     Trường sắp xếp (mặc định là createdAt)
     * @param sortDir    Hướng sắp xếp (mặc định là desc)
     * @return Danh sách sản phẩm phù hợp
     */
    @GetMapping()
    @Operation(summary = "Search products", description = "Search products with multiple filters")
    public ResponseEntity<BaseResponse<PageResponse<ProductViewDTO>>> searchProducts(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "isOwner", defaultValue = "false", required = false) boolean isOwner,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {
        PageResponse<ProductViewDTO> page = productViewService.searchProducts(isOwner,shopId, categoryId, status, keyword, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(
                BaseResponse.<PageResponse<ProductViewDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.PRODUCT_RETRIEVED_SUCCESS))
                        .data(page)
                        .build()
        );
    }

    /**
     * Lấy thông tin sản phẩm theo ID
     *
     * @param productId ID của sản phẩm
     * @return Thông tin sản phẩm
     */
    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID", description = "Retrieve product details by product ID")
    public ResponseEntity<BaseResponse<ProductViewDTO>> getProductById(
            @PathVariable Long productId,
            @RequestParam(value = "isOwner", defaultValue = "false", required = false) boolean isOwner
            ) {
        ProductViewDTO productResponse = productViewService.getProductById(productId, isOwner);

        return ResponseEntity.ok(BaseResponse.<ProductViewDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_RETRIEVED_SUCCESS))
                .data(productResponse)
                .build());
    }

}
