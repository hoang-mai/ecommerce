package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.ProductViewStatisticDTO;
import com.ecommerce.read.entity.ProductView;
import com.ecommerce.read.service.ProductViewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
     * @param star       Đánh giá sao tối thiểu (optional)
     * @param startPrice Giá bắt đầu (optional)
     * @param endPrice   Giá kết thúc (optional)
     * @param isOwner    Xác định người dùng hiện tại có phải là chủ sở hữu sản phẩm không (mặc định là false)
     * @param keyword    Từ khóa tìm kiếm (optional)
     * @param pageNo     Số trang (mặc định là 0)
     * @param pageSize   Kích thước trang (mặc định là 10)
     * @param sortBy     Trường sắp xếp (mặc định là createdAt)
     * @param sortDir    Hướng sắp xếp (mặc định là desc)
     * @return Danh sách sản phẩm phù hợp
     */
    @GetMapping()
    @Operation(summary = "Search products", description = "Search products with multiple filters")
    public ResponseEntity<BaseResponse<PageResponse<ProductView>>> searchProducts(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer star,
            @RequestParam(required = false) Double startPrice,
            @RequestParam(required = false) Double endPrice,
            @RequestParam(required = false) Boolean isOwner,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {
        PageResponse<ProductView> page = productViewService.searchProducts(isOwner, shopId, categoryId, status, keyword, star, startPrice, endPrice, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(
                BaseResponse.<PageResponse<ProductView>>builder()
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
    public ResponseEntity<BaseResponse<ProductView>> getProductById(
            @PathVariable Long productId,
            @RequestParam(value = "isOwner", defaultValue = "false", required = false) boolean isOwner
    ) {
        ProductView productResponse = productViewService.getProductById(productId, isOwner);

        return ResponseEntity.ok(BaseResponse.<ProductView>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_RETRIEVED_SUCCESS))
                .data(productResponse)
                .build());
    }
    /**
     * Thống kê sản phẩm bán chạy hoặc doanh thu cao trong tháng
     * @param shopId  ID của cửa hàng (optional) - nếu cung cấp sẽ trả về thống kê cho cửa hàng đó (chỉ owner của shop)
     * @param isOwner Xác định người dùng hiện tại có phải là chủ sở hữu shop không (optional)
     * @param nowDate Thời điểm hiện tại để xác định tháng (optional, mặc định là thời điểm hiện tại)
     * @param type    Loại thống kê: "sold" (bán chạy - mặc định) hoặc "revenue" (doanh thu cao)
     * @return Thống kê sản phẩm (top 5)
     */
    @GetMapping("/statistic")
    public ResponseEntity<BaseResponse<List<ProductViewStatisticDTO>>> getProductStatistics(
        @RequestParam(required = false) String shopId,
        @RequestParam(required = false) Boolean isOwner,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime nowDate,
        @RequestParam(required = false, defaultValue = "sold") String type
    ) {
        List<ProductViewStatisticDTO> stats = productViewService.getProductStatistics(shopId, isOwner, nowDate, type);
        return ResponseEntity.ok(
            BaseResponse.<List<ProductViewStatisticDTO>>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.GET_PRODUCT_SUCCESS))
                .data(stats)
                .build()
        );
    }
}
