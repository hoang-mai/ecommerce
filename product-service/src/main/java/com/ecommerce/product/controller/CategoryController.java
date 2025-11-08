package com.ecommerce.product.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.ReqCreateCategoryDTO;
import com.ecommerce.product.dto.ReqUpdateCategoryDTO;
import com.ecommerce.product.dto.ReqUpdateCategoryStatusDTO;
import com.ecommerce.product.dto.ResCategoryDTO;
import com.ecommerce.product.dto.ResCategorySearchDTO;
import com.ecommerce.library.enumeration.CategoryStatus;
import com.ecommerce.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constant.CATEGORY)
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "APIs for managing product categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final MessageService messageService;

    /**
     * Tạo category mới
     *
     * @param request Thông tin category cần tạo
     * @return Thông tin category đã được tạo
     */
    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a new product category")
    public ResponseEntity<BaseResponse<Void>> createCategory(
            @Valid @RequestBody ReqCreateCategoryDTO request) {
        categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.CATEGORY_CREATED_SUCCESS))
                .build());
    }

    /**
     * Lấy thông tin category theo ID
     *
     * @param categoryId ID của category
     * @return Thông tin category
     */
    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID", description = "Retrieve category details by category ID")
    public ResponseEntity<BaseResponse<ResCategoryDTO>> getCategoryById(@PathVariable Long categoryId) {
        ResCategoryDTO categoryResponse = categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(BaseResponse.<ResCategoryDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.CATEGORY_RETRIEVED_SUCCESS))
                .data(categoryResponse)
                .build());
    }

    /**
     * Cập nhật thông tin category
     *
     * @param categoryId ID của category cần cập nhật
     * @param request Thông tin cập nhật
     * @return Thông tin category đã được cập nhật
     */
    @PatchMapping("/{categoryId}")
    @Operation(summary = "Update category", description = "Update category information")
    public ResponseEntity<BaseResponse<Void>> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody ReqUpdateCategoryDTO request) {
        categoryService.updateCategory(categoryId, request);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.CATEGORY_UPDATED_SUCCESS))
                .build());
    }

    /**
     * Cập nhật trạng thái category
     *
     * @param categoryId ID của category cần cập nhật
     * @param request Thông tin trạng thái mới
     * @return Response thành công
     */
    @PatchMapping("/{categoryId}/status")
    @Operation(summary = "Update category status", description = "Update category status")
    public ResponseEntity<BaseResponse<Void>> updateCategoryStatus(
            @PathVariable Long categoryId,
            @Valid @RequestBody ReqUpdateCategoryStatusDTO request) {
        categoryService.updateCategoryStatus(categoryId, request);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.CATEGORY_STATUS_UPDATED_SUCCESS))
                .build());
    }

    /**
     * Tìm kiếm categories theo tên với phân trang
     *
     * @param keyword Từ khóa tìm kiếm (optional)
     * @param status Trạng thái category (optional)
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy Trường sắp xếp (mặc định là categoryName)
     * @param sortDir Hướng sắp xếp (mặc định là asc)
     * @return Danh sách categories phù hợp
     */
    @GetMapping("/search")
    @Operation(summary = "Search categories", description = "Search categories by name with pagination")
    public ResponseEntity<BaseResponse<PageResponse<ResCategorySearchDTO>>> searchCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CategoryStatus status,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "categoryId", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {

        PageResponse<ResCategorySearchDTO> pageResponse = categoryService.searchCategories(
                keyword, status, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(BaseResponse.<PageResponse<ResCategorySearchDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.CATEGORIES_RETRIEVED_SUCCESS))
                .data(pageResponse)
                .build());
    }

}

