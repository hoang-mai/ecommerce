package com.ecommerce.product.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.ProductVariantStatus;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.*;
import com.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(Constant.PRODUCT)
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    private final ProductService productService;
    private final MessageService messageService;

    /**
     * Tạo sản phẩm mới
     *
     * @param files Danh sách ảnh sản phẩm
     * @param request Thông tin sản phẩm cần tạo
     */
    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Create a new product", description = "Create a new product with details")
    public ResponseEntity<BaseResponse<Void>> createProduct(
            @RequestParam(value = "imageUrls") List<MultipartFile> files,
            @Valid @RequestPart(value = "data") ReqCreateProductDTO request) {
        productService.createProduct(request, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_CREATED_SUCCESS))
                .build());
    }



    /**
     * Cập nhật thông tin sản phẩm
     *
     * @param productId ID của sản phẩm cần cập nhật
     * @param files Danh sách ảnh sản phẩm mới (optional)
     * @param request Thông tin cập nhật
     * @return Thông tin sản phẩm đã được cập nhật
     */
    @PatchMapping(value = "/{productId}", consumes = "multipart/form-data")
    @Operation(summary = "Update product", description = "Update product information with optional new images")
    public ResponseEntity<BaseResponse<Void>> updateProduct(
            @PathVariable Long productId,
            @RequestParam(value = "imageUrls", required = false) List<MultipartFile> files,
            @Valid @RequestPart(value = "data") ReqUpdateProductDTO request) {
        productService.updateProduct(productId, request, files);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_UPDATED_SUCCESS))
                .build());
    }

    /**
     * Cập nhật trạng thái biến thể sản phẩm
     *
     * @param productVariantId ID của sản phẩm cần cập nhật trạng thái
     * @param request Trạng thái mới
     * @return Kết quả cập nhật
     */
    @PatchMapping("/{productVariantId}/status")
    @Operation(summary = "Update product status", description = "Update product status (ACTIVE, INACTIVE, OUT_OF_STOCK)")
    public ResponseEntity<BaseResponse<Void>> updateProductStatus(
            @PathVariable Long productVariantId,
            @Valid @RequestBody ReqUpdateProductVariantStatusDTO request) {
        productService.updateProductVariantStatus(productVariantId, request);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_STATUS_UPDATED_SUCCESS))
                .build());
    }

    /**
     * Cập nhật trạng thái sản phẩm
     *
     * @param productId ID của sản phẩm cần cập nhật trạng thái
     * @param status Trạng thái mới
     * @return Kết quả cập nhật
     */
    @PatchMapping("/{productId}/product-status")
    @Operation(summary = "Update product status by product ID", description = "Update product status by product ID")
    public ResponseEntity<BaseResponse<Void>> updateProductStatusByProductId(
            @PathVariable Long productId,
            @RequestBody @Valid ReqUpdateProductStatusDTO status) {
        productService.updateProductStatusByProductId(productId, status);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_STATUS_UPDATED_SUCCESS))
                .build());
    }

}