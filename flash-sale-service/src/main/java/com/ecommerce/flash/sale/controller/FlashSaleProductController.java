package com.ecommerce.flash.sale.controller;

import com.ecommerce.flash.sale.dto.ReqCreateFlashSaleProductDTO;
import com.ecommerce.flash.sale.dto.ReqUpdateFlashSaleProductDTO;
import com.ecommerce.flash.sale.service.FlashSaleProductService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(Constant.FLASH_SALE_PRODUCT)
@RequiredArgsConstructor
@Tag(name = "Flash Sale Product Management", description = "APIs for managing flash sale products with discount validation (20-70%) and scoring")
public class FlashSaleProductController {

    private final FlashSaleProductService flashSaleProductService;
    private final MessageService messageService;

    /**
     * Tạo mới flash sale product
     * Yêu cầu: Phần trăm giảm giá phải từ 20% đến 70%
     * Tính điểm dựa trên: discount (40%), rating (30%), totalSold (20%), totalQuantity (10%)
     *
     * @param request Thông tin flash sale product cần tạo
     */
    @PostMapping
    @Operation(summary = "Create flash sale product",
        description = "Create a new flash sale product with discount validation (20-70%) and automatic scoring")
    public ResponseEntity<BaseResponse<Void>> createFlashSaleProduct(
        @Valid @RequestBody ReqCreateFlashSaleProductDTO request) {
        flashSaleProductService.createFlashSaleProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
            .statusCode(HttpStatus.CREATED.value())
            .message(messageService.getMessage(MessageSuccess.FLASH_SALE_PRODUCT_CREATED_SUCCESS))
            .build());
    }

    /**
     * Cập nhật flash sale product
     * Yêu cầu: startTime của campaign phải > ngày hiện tại
     *
     * @param flashSaleProductId ID của flash sale product cần cập nhật
     * @param request Thông tin cập nhật
     */
    @PatchMapping("/{flashSaleProductId}")
    @Operation(summary = "Update flash sale product",
        description = "Update a flash sale product only if campaign startTime is greater than current date")
    public ResponseEntity<BaseResponse<Void>> updateFlashSaleProduct(
        @PathVariable Long flashSaleProductId,
        @Valid @RequestBody ReqUpdateFlashSaleProductDTO request) {
        flashSaleProductService.updateFlashSaleProduct(flashSaleProductId, request);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
            .statusCode(HttpStatus.OK.value())
            .message(messageService.getMessage(MessageSuccess.FLASH_SALE_PRODUCT_UPDATED_SUCCESS))
            .build());
    }

    /**
     * Xóa flash sale product
     * Yêu cầu: startTime của campaign phải > ngày hiện tại
     *
     * @param flashSaleProductId ID của flash sale product cần xóa
     */
    @DeleteMapping("/{flashSaleProductId}")
    @Operation(summary = "Delete flash sale product",
        description = "Delete a flash sale product only if campaign startTime is greater than current date")
    public ResponseEntity<BaseResponse<Void>> deleteFlashSaleProduct(
        @PathVariable Long flashSaleProductId) {
        flashSaleProductService.deleteFlashSaleProduct(flashSaleProductId);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
            .statusCode(HttpStatus.OK.value())
            .message(messageService.getMessage(MessageSuccess.FLASH_SALE_PRODUCT_DELETED_SUCCESS))
            .build());
    }


}

