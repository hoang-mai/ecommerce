package com.ecommerce.product.service;

import com.ecommerce.product.dto.ReqUpdateProductVariantDTO;
import com.ecommerce.product.dto.ResProductVariantDTO;

import java.util.List;

public interface ProductVariantService {

    /**
     * Lấy thông tin variant theo ID
     *
     * @param variantId ID của variant
     * @return Thông tin variant
     */
    ResProductVariantDTO getVariantById(Long variantId);

    /**
     * Lấy danh sách variants của một sản phẩm
     *
     * @param productId ID của sản phẩm
     * @return Danh sách variants
     */
    List<ResProductVariantDTO> getVariantsByProductId(Long productId);

    /**
     * Cập nhật thông tin variant
     *
     * @param variantId ID của variant cần cập nhật
     * @param request Thông tin cập nhật
     * @return Thông tin variant đã được cập nhật
     */
    ResProductVariantDTO updateVariant(Long variantId, ReqUpdateProductVariantDTO request);

    /**
     * Xóa variant
     *
     * @param variantId ID của variant cần xóa
     */
    void deleteVariant(Long variantId);

    /**
     * Cập nhật số lượng tồn kho
     *
     * @param variantId ID của variant
     * @param quantity Số lượng thay đổi (có thể âm hoặc dương)
     * @return Thông tin variant đã được cập nhật
     */
    ResProductVariantDTO updateStock(Long variantId, Integer quantity);
}

