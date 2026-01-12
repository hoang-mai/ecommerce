package com.ecommerce.flash.sale.controller;

import com.ecommerce.flash.sale.dto.ResPurchaseLimitCheckDTO;
import com.ecommerce.flash.sale.service.UserPurchaseLimitService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constant.USER_PURCHASE_LIMIT)
@RequiredArgsConstructor
@Tag(name = "User Purchase Limit", description = "APIs for checking user purchase limits in flash sale")
public class UserPurChaseLimitController {

    private final UserPurchaseLimitService userPurchaseLimitService;
    private final MessageService messageService;

    /**
     * Kiểm tra user có vượt mức mua cho phép không với danh sách product variant
     *
     * @param productVariantIds Danh sách product variant id cần kiểm tra
     * @return Danh sách thông tin giới hạn mua của user cho từng product variant
     */
    @GetMapping
    @Operation(summary = "Check user purchase limit",
            description = "Check if user has exceeded purchase limit for given product variants in flash sale")
    public ResponseEntity<BaseResponse<List<ResPurchaseLimitCheckDTO>>> checkPurchaseLimit(
            @RequestParam List<Long> productVariantIds) {
        List<ResPurchaseLimitCheckDTO> result = userPurchaseLimitService.checkPurchaseLimit(productVariantIds);

        return ResponseEntity.ok(BaseResponse.<List<ResPurchaseLimitCheckDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.USER_PURCHASE_LIMIT_CHECKED_SUCCESS))
                .data(result)
                .build());
    }
}
