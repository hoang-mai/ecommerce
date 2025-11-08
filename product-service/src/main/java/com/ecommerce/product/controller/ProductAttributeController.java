package com.ecommerce.product.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.product.dto.ReqCreateProductAttributeDTO;
import com.ecommerce.product.dto.ReqUpdateProductAttributeDTO;
import com.ecommerce.product.dto.ResProductAttributeDTO;
import com.ecommerce.product.service.ProductAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constant.PRODUCT_ATTRIBUTE)
@RequiredArgsConstructor
@Tag(name = "Product Attribute Management", description = "APIs for managing product attributes")
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;
    private final MessageService messageService;

    /**
     * Tạo mới một product attribute
     *
     * @param request Request DTO chứa thông tin product attribute
     */
    @PostMapping
    @Operation(summary = "Create product attribute", description = "Create a new product attribute with its values")
    public ResponseEntity<BaseResponse<Void>> createProductAttribute(
            @Valid @RequestBody ReqCreateProductAttributeDTO request) {
        productAttributeService.createProductAttribute(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageService.getMessage(MessageSuccess.PRODUCT_ATTRIBUTE_CREATED_SUCCESS))
                        .build());
    }

    /**
     * Lấy thông tin product attribute theo ID
     *
     * @param attributeId ID của product attribute
     * @return Thông tin product attribute
     */
    @GetMapping("/{attributeId}")
    @Operation(summary = "Get product attribute by ID", description = "Retrieve product attribute details by attribute ID")
    public ResponseEntity<BaseResponse<ResProductAttributeDTO>> getProductAttributeById(@PathVariable Long attributeId) {
        ResProductAttributeDTO attributeResponse = productAttributeService.getProductAttributeById(attributeId);

        return ResponseEntity.ok(BaseResponse.<ResProductAttributeDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product attribute retrieved successfully")
                .data(attributeResponse)
                .build());
    }

    /**
     * Lấy danh sách product attributes theo product ID
     *
     * @param productId ID của product
     * @return Danh sách product attributes
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product attributes by product ID", description = "Retrieve all attributes for a specific product")
    public ResponseEntity<BaseResponse<List<ResProductAttributeDTO>>> getProductAttributesByProductId(@PathVariable Long productId) {
        List<ResProductAttributeDTO> attributes = productAttributeService.getProductAttributesByProductId(productId);

        return ResponseEntity.ok(BaseResponse.<List<ResProductAttributeDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product attributes retrieved successfully")
                .data(attributes)
                .build());
    }

    /**
     * Cập nhật thông tin product attribute
     *
     * @param attributeId ID của product attribute cần cập nhật
     * @param request Thông tin cập nhật
     * @return Thông tin product attribute đã được cập nhật
     */
    @PatchMapping("/{attributeId}")
    @Operation(summary = "Update product attribute", description = "Update product attribute information and its values")
    public ResponseEntity<BaseResponse<Void>> updateProductAttribute(
            @PathVariable Long attributeId,
            @Valid @RequestBody ReqUpdateProductAttributeDTO request) {
        productAttributeService.updateProductAttribute(attributeId, request);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product attribute updated successfully")
                .build());
    }

    /**
     * Xóa product attribute
     *
     * @param attributeId ID của product attribute cần xóa
     * @return Thông báo thành công
     */
    @DeleteMapping("/{attributeId}")
    @Operation(summary = "Delete product attribute", description = "Delete a product attribute by ID")
    public ResponseEntity<BaseResponse<Void>> deleteProductAttribute(@PathVariable Long attributeId) {
        productAttributeService.deleteProductAttribute(attributeId);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product attribute deleted successfully")
                .build());
    }
}

