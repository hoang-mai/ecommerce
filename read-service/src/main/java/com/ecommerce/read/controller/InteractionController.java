package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.CreateInteractionRequest;
import com.ecommerce.read.dto.InteractionDTO;
import com.ecommerce.read.dto.InteractionStatisticDTO;
import com.ecommerce.read.entity.Interaction;
import com.ecommerce.read.service.InteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = Constant.INTERACTION)
@RequiredArgsConstructor
@Tag(name = "Interaction", description = "APIs for managing user interactions with products")
public class InteractionController {

    private final InteractionService interactionService;
    private final MessageService messageService;

    /**
     * Tạo mới một tương tác (view, click, favorite, etc.)
     */
    @PostMapping
    @Operation(summary = "Create interaction", description = "Create a new user interaction with a product")
    public ResponseEntity<BaseResponse<Void>> createInteraction(
            @Valid @RequestBody CreateInteractionRequest request) {
        interactionService.createInteraction(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_CREATED_SUCCESS))
                        .build()
        );
    }

    /**
     * Lấy tất cả tương tác của một user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user interactions", description = "Get all interactions of a specific user")
    public ResponseEntity<BaseResponse<List<InteractionDTO>>> getInteractionsByUserId(
            @PathVariable String userId) {
        List<InteractionDTO> interactions = interactionService.getInteractionsByUserId(userId);

        return ResponseEntity.ok(
                BaseResponse.<List<InteractionDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(interactions)
                        .build()
        );
    }

    /**
     * Lấy tất cả tương tác với một sản phẩm (có phân trang)
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product interactions", description = "Get all interactions with a specific product (paginated)")
    public ResponseEntity<BaseResponse<PageResponse<InteractionDTO>>> getInteractionsByProductId(
            @PathVariable String productId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {
        PageResponse<InteractionDTO> page = interactionService.getInteractionsByProductId(
                productId, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(
                BaseResponse.<PageResponse<InteractionDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(page)
                        .build()
        );
    }

    /**
     * Lấy tương tác của user với một sản phẩm cụ thể
     */
    @GetMapping("/user/{userId}/product/{productId}")
    @Operation(summary = "Get user-product interactions", description = "Get interactions of a user with a specific product")
    public ResponseEntity<BaseResponse<List<InteractionDTO>>> getInteractionsByUserAndProduct(
            @PathVariable String userId,
            @PathVariable String productId) {
        List<InteractionDTO> interactions = interactionService.getInteractionsByUserAndProduct(userId, productId);

        return ResponseEntity.ok(
                BaseResponse.<List<InteractionDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(interactions)
                        .build()
        );
    }

    /**
     * Lấy tương tác theo loại (type)
     */
    @GetMapping("/type/{interactionType}")
    @Operation(summary = "Get interactions by type", description = "Get all interactions of a specific type")
    public ResponseEntity<BaseResponse<List<InteractionDTO>>> getInteractionsByType(
            @PathVariable Interaction.InteractionType interactionType) {
        List<InteractionDTO> interactions = interactionService.getInteractionsByType(interactionType);

        return ResponseEntity.ok(
                BaseResponse.<List<InteractionDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(interactions)
                        .build()
        );
    }

    /**
     * Lấy tương tác của user theo loại
     */
    @GetMapping("/user/{userId}/type/{interactionType}")
    @Operation(summary = "Get user interactions by type", description = "Get interactions of a user filtered by type")
    public ResponseEntity<BaseResponse<List<InteractionDTO>>> getInteractionsByUserAndType(
            @PathVariable String userId,
            @PathVariable Interaction.InteractionType interactionType) {
        List<InteractionDTO> interactions = interactionService.getInteractionsByUserAndType(userId, interactionType);

        return ResponseEntity.ok(
                BaseResponse.<List<InteractionDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(interactions)
                        .build()
        );
    }

    /**
     * Cập nhật metadata của tương tác
     */
    @PatchMapping("/{interactionId}")
    @Operation(summary = "Update interaction metadata", description = "Update the metadata of an interaction")
    public ResponseEntity<BaseResponse<InteractionDTO>> updateInteraction(
            @PathVariable String interactionId,
            @RequestBody InteractionDTO.InteractionMetadataDTO metadata) {
        InteractionDTO updated = interactionService.updateInteraction(interactionId, metadata);

        return ResponseEntity.ok(
                BaseResponse.<InteractionDTO>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_UPDATED_SUCCESS))
                        .data(updated)
                        .build()
        );
    }

    /**
     * Đếm số lượng tương tác của sản phẩm theo type
     */
    @GetMapping("/product/{productId}/type/{interactionType}/count")
    @Operation(summary = "Count product interactions by type",
               description = "Count the number of interactions for a product by type")
    public ResponseEntity<BaseResponse<Long>> countInteractionsByProductAndType(
            @PathVariable String productId,
            @PathVariable Interaction.InteractionType interactionType) {
        Long count = interactionService.countInteractionsByProductAndType(productId, interactionType);

        return ResponseEntity.ok(
                BaseResponse.<Long>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(count)
                        .build()
        );
    }

    /**
     * Lấy thống kê tương tác của sản phẩm
     */
    @GetMapping("/product/{productId}/statistics")
    @Operation(summary = "Get product interaction statistics",
               description = "Get complete interaction statistics for a product")
    public ResponseEntity<BaseResponse<Map<Interaction.InteractionType, Long>>> getProductInteractionStatistics(
            @PathVariable String productId) {
        Map<Interaction.InteractionType, Long> statistics =
                interactionService.getProductInteractionStatistics(productId);

        return ResponseEntity.ok(
                BaseResponse.<Map<Interaction.InteractionType, Long>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(statistics)
                        .build()
        );
    }

    /**
     * Kiểm tra xem user đã tương tác với product chưa
     */
    @GetMapping("/check")
    @Operation(summary = "Check if user interacted with product",
               description = "Check if a user has interacted with a product using a specific type")
    public ResponseEntity<BaseResponse<Boolean>> hasUserInteractedWithProduct(
            @RequestParam String userId,
            @RequestParam String productId,
            @RequestParam Interaction.InteractionType interactionType) {
        boolean hasInteracted = interactionService.hasUserInteractedWithProduct(userId, productId, interactionType);

        return ResponseEntity.ok(
                BaseResponse.<Boolean>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(hasInteracted)
                        .build()
        );
    }

    /**
     * Lấy danh sách sản phẩm yêu thích của user
     */
    @GetMapping("/user/{userId}/favorites")
    @Operation(summary = "Get user favorite products", description = "Get all favorite products of a user")
    public ResponseEntity<BaseResponse<List<InteractionDTO>>> getUserFavoriteProducts(
            @PathVariable String userId) {
        List<InteractionDTO> favorites = interactionService.getUserFavoriteProducts(userId);

        return ResponseEntity.ok(
                BaseResponse.<List<InteractionDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(favorites)
                        .build()
        );
    }

    /**
     * Lấy lịch sử xem sản phẩm của user
     */
    @GetMapping("/user/{userId}/history")
    @Operation(summary = "Get user view history", description = "Get the product view history of a user")
    public ResponseEntity<BaseResponse<List<InteractionDTO>>> getUserViewHistory(
            @PathVariable String userId) {
        List<InteractionDTO> history = interactionService.getUserViewHistory(userId);

        return ResponseEntity.ok(
                BaseResponse.<List<InteractionDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(history)
                        .build()
        );
    }

    /**
     * Lấy thống kê tương tác theo khoảng thời gian
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get interaction statistics by date range",
               description = "Get interaction statistics within a specific date range")
    public ResponseEntity<BaseResponse<List<InteractionStatisticDTO>>> getInteractionStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<InteractionStatisticDTO> statistics =
                interactionService.getInteractionStatistics(startDate, endDate);

        return ResponseEntity.ok(
                BaseResponse.<List<InteractionStatisticDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.INTERACTION_RETRIEVED_SUCCESS))
                        .data(statistics)
                        .build()
        );
    }
}

