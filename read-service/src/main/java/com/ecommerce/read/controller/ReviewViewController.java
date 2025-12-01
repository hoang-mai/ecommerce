package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.ReviewView;
import com.ecommerce.read.service.ReviewViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constant.REVIEW_VIEW)
@RequiredArgsConstructor
public class ReviewViewController {
    private final ReviewViewService reviewViewService;
    private final MessageService messageService;

    /**
     * Lấy rating của product theo productId
     *
     * @param stars     Số sao đánh giá (optionnal)
     * @param productId ID của product
     * @param pageNo    Trang hiện tại
     * @param pageSize  Số phần tử trên mỗi trang
     * @param sortBy    Thuộc tính để sắp xếp
     * @param sortDir   Hướng sắp xếp (asc hoặc desc)
     * @return Danh sách rating của product
     *
     */
    @GetMapping("/{productId}")
    public ResponseEntity<BaseResponse<PageResponse<ReviewView>>> getProductRating(
            @PathVariable Long productId,
            @RequestParam(required = false) Integer stars,
            @RequestParam(required = false, defaultValue = "0") int pageSize,
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(
                BaseResponse.<PageResponse<ReviewView>>builder()
                        .message(messageService.getMessage(MessageSuccess.REVIEW_VIEW_GET_SUCCESS))
                        .statusCode(HttpStatus.OK.value())
                        .data(reviewViewService.getReviewsByProductId(productId, stars, pageNo, pageSize, sortBy, sortDir))
                        .build()
        );
    }
}
