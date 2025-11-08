package com.ecommerce.product.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.product.dto.ReqCreateProductImageDTO;
import com.ecommerce.product.dto.ReqUpdateProductImageDTO;
import com.ecommerce.product.dto.ResProductImageDTO;
import com.ecommerce.product.service.IProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constant.PRODUCT_IMAGE)
@RequiredArgsConstructor
@Tag(name = "Product Image Management", description = "APIs for managing product images")
public class ProductImageController {

    private final IProductImageService productImageService;
    private final MessageService messageService;

    /**
     * Tạo mới một product image
     *
     * @param request Request DTO chứa thông tin product image
     * @return ResProductImageDTO
     */
    @PostMapping
    @Operation(summary = "Create a new product image", description = "Create a new product image")
    public ResponseEntity<BaseResponse<ResProductImageDTO>> createProductImage(
            @Valid @RequestBody ReqCreateProductImageDTO request) {
        ResProductImageDTO response = productImageService.createProductImage(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<ResProductImageDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_IMAGE_CREATED_SUCCESS))
                .data(response)
                .build());
    }

    /**
     * Lấy thông tin product image theo ID
     *
     * @param imageId ID của product image
     * @return ResProductImageDTO
     */
    @GetMapping("/{imageId}")
    @Operation(summary = "Get product image by ID", description = "Retrieve product image details by image ID")
    public ResponseEntity<BaseResponse<ResProductImageDTO>> getProductImageById(@PathVariable Long imageId) {
        ResProductImageDTO response = productImageService.getProductImageById(imageId);

        return ResponseEntity.ok(BaseResponse.<ResProductImageDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_IMAGE_RETRIEVED_SUCCESS))
                .data(response)
                .build());
    }

    /**
     * Lấy danh sách product images theo product ID
     *
     * @param productId ID của product
     * @return List of ResProductImageDTO
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product images by product ID", description = "Retrieve all images for a specific product")
    public ResponseEntity<BaseResponse<List<ResProductImageDTO>>> getProductImagesByProductId(
            @PathVariable Long productId) {
        List<ResProductImageDTO> response = productImageService.getProductImagesByProductId(productId);

        return ResponseEntity.ok(BaseResponse.<List<ResProductImageDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_IMAGES_RETRIEVED_SUCCESS))
                .data(response)
                .build());
    }

    /**
     * Cập nhật thông tin product image
     *
     * @param imageId ID của product image cần cập nhật
     * @param request Request DTO chứa thông tin cập nhật
     * @return ResProductImageDTO
     */
    @PatchMapping("/{imageId}")
    @Operation(summary = "Update product image", description = "Update product image information")
    public ResponseEntity<BaseResponse<ResProductImageDTO>> updateProductImage(
            @PathVariable Long imageId,
            @Valid @RequestBody ReqUpdateProductImageDTO request) {
        ResProductImageDTO response = productImageService.updateProductImage(imageId, request);

        return ResponseEntity.ok(BaseResponse.<ResProductImageDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_IMAGE_UPDATED_SUCCESS))
                .data(response)
                .build());
    }

    /**
     * Xóa product image
     *
     * @param imageId ID của product image cần xóa
     */
    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete product image", description = "Delete a product image by ID")
    public ResponseEntity<BaseResponse<Void>> deleteProductImage(@PathVariable Long imageId) {
        productImageService.deleteProductImage(imageId);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.PRODUCT_IMAGE_DELETED_SUCCESS))
                .build());
    }
}

