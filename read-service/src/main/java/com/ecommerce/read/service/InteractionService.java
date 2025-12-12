package com.ecommerce.read.service;

import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.CreateInteractionRequest;
import com.ecommerce.read.dto.InteractionDTO;
import com.ecommerce.read.dto.InteractionStatisticDTO;
import com.ecommerce.read.entity.Interaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface InteractionService {

    /**
     * Tạo mới một tương tác
     */
    void createInteraction(CreateInteractionRequest request);

    /**
     * Lấy tất cả tương tác của một user
     */
    List<InteractionDTO> getInteractionsByUserId(String userId);

    /**
     * Lấy tất cả tương tác với một sản phẩm (có phân trang)
     */
    PageResponse<InteractionDTO> getInteractionsByProductId(String productId, int pageNo, int pageSize, String sortBy, String sortDir);

    /**
     * Lấy tương tác của user với một sản phẩm
     */
    List<InteractionDTO> getInteractionsByUserAndProduct(String userId, String productId);

    /**
     * Lấy tương tác theo type
     */
    List<InteractionDTO> getInteractionsByType(Interaction.InteractionType interactionType);

    /**
     * Lấy tương tác của user theo type
     */
    List<InteractionDTO> getInteractionsByUserAndType(String userId, Interaction.InteractionType interactionType);

    /**
     * Xóa tương tác (ví dụ: unfavorite, remove from cart)
     */
    void deleteInteraction(String interactionId);

    /**
     * Xóa tương tác theo user, product và type
     */
    void deleteInteractionByUserProductType(String userId, String productId, Interaction.InteractionType interactionType);

    /**
     * Cập nhật metadata của tương tác
     */
    InteractionDTO updateInteraction(String interactionId, InteractionDTO.InteractionMetadataDTO metadata);

    /**
     * Đếm số lượng tương tác của một sản phẩm theo type
     */
    Long countInteractionsByProductAndType(String productId, Interaction.InteractionType interactionType);

    /**
     * Lấy thống kê tương tác của sản phẩm
     */
    Map<Interaction.InteractionType, Long> getProductInteractionStatistics(String productId);

    /**
     * Kiểm tra xem user đã tương tác với product chưa
     */
    boolean hasUserInteractedWithProduct(String userId, String productId, Interaction.InteractionType interactionType);

    /**
     * Lấy sản phẩm yêu thích của user
     */
    List<InteractionDTO> getUserFavoriteProducts(String userId);

    /**
     * Lấy lịch sử xem sản phẩm của user
     */
    List<InteractionDTO> getUserViewHistory(String userId);

    /**
     * Lấy thống kê tương tác theo khoảng thời gian
     */
    List<InteractionStatisticDTO> getInteractionStatistics(LocalDateTime startDate, LocalDateTime endDate);
}

