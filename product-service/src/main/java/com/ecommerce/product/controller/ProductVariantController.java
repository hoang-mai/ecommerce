package com.ecommerce.product.controller;

import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.product.dto.ReqUpdateProductVariantDTO;
import com.ecommerce.product.dto.ResProductVariantDTO;
import com.ecommerce.product.service.ProductVariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-variants")
@RequiredArgsConstructor
@Tag(name = "Product Variant Management", description = "APIs for managing product variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    /**
     * Lấy thông tin variant theo ID
     *
     * @param variantId ID của variant
     * @return Thông tin variant
     */
    @GetMapping("/{variantId}")
    @Operation(summary = "Get variant by ID", description = "Retrieve product variant details by variant ID")
    public ResponseEntity<BaseResponse<ResProductVariantDTO>> getVariantById(@PathVariable Long variantId) {
        ResProductVariantDTO variantResponse = productVariantService.getVariantById(variantId);

        return ResponseEntity.ok(BaseResponse.<ResProductVariantDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product variant retrieved successfully")
                .data(variantResponse)
                .build());
    }

    /**
     * Lấy danh sách variants của một sản phẩm
     *
     * @param productId ID của sản phẩm
     * @return Danh sách variants
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get variants by product ID", description = "Retrieve all variants for a specific product")
    public ResponseEntity<BaseResponse<List<ResProductVariantDTO>>> getVariantsByProductId(@PathVariable Long productId) {
        List<ResProductVariantDTO> variants = productVariantService.getVariantsByProductId(productId);

        return ResponseEntity.ok(BaseResponse.<List<ResProductVariantDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product variants retrieved successfully")
                .data(variants)
                .build());
    }

    /**
     * Cập nhật thông tin variant
     *
     * @param variantId ID của variant cần cập nhật
     * @param request Thông tin cập nhật
     * @return Thông tin variant đã được cập nhật
     */
    @PutMapping("/{variantId}")
    @Operation(summary = "Update variant", description = "Update product variant information")
    public ResponseEntity<BaseResponse<ResProductVariantDTO>> updateVariant(
            @PathVariable Long variantId,
            @Valid @RequestBody ReqUpdateProductVariantDTO request) {
        ResProductVariantDTO variantResponse = productVariantService.updateVariant(variantId, request);

        return ResponseEntity.ok(BaseResponse.<ResProductVariantDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product variant updated successfully")
                .data(variantResponse)
                .build());
    }

    /**
     * Xóa variant
     *
     * @param variantId ID của variant cần xóa
     * @return Thông báo thành công
     */
    @DeleteMapping("/{variantId}")
    @Operation(summary = "Delete variant", description = "Delete a product variant by ID")
    public ResponseEntity<BaseResponse<Void>> deleteVariant(@PathVariable Long variantId) {
        productVariantService.deleteVariant(variantId);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product variant deleted successfully")
                .build());
    }

    /**
     * Cập nhật số lượng tồn kho
     *
     * @param variantId ID của variant
     * @param quantity Số lượng thay đổi
     * @return Thông tin variant đã được cập nhật
     */
    @PatchMapping("/{variantId}/stock")
    @Operation(summary = "Update stock", description = "Update stock quantity for a product variant")
    public ResponseEntity<BaseResponse<ResProductVariantDTO>> updateStock(
            @PathVariable Long variantId,
            @RequestParam Integer quantity) {
        ResProductVariantDTO variantResponse = productVariantService.updateStock(variantId, quantity);

        return ResponseEntity.ok(BaseResponse.<ResProductVariantDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Stock updated successfully")
                .data(variantResponse)
                .build());
    }
}


