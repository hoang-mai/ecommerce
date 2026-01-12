package com.ecommerce.flash.sale.service;

import com.ecommerce.flash.sale.dto.ResPurchaseLimitCheckDTO;

import java.util.List;

public interface UserPurchaseLimitService {

    /**
     * Kiểm tra user có vượt mức mua cho phép không với danh sách product variant
     *
     * @param productVariantIds Danh sách product variant id cần kiểm tra
     * @return Danh sách thông tin giới hạn mua của user cho từng product variant
     */
    List<ResPurchaseLimitCheckDTO> checkPurchaseLimit(List<Long> productVariantIds);
}

