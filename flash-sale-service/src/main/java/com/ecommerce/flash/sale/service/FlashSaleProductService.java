package com.ecommerce.flash.sale.service;

import com.ecommerce.flash.sale.dto.ReqCreateFlashSaleProductDTO;
import com.ecommerce.flash.sale.dto.ReqUpdateFlashSaleProductDTO;

public interface FlashSaleProductService {

    /**
     * Tạo mới flash sale product với validation discount 20-70%
     * và tính điểm dựa trên rating, số lượng bán, tỷ lệ giảm giá
     *
     * @param request Thông tin flash sale product
     */
    void createFlashSaleProduct(ReqCreateFlashSaleProductDTO request);

    /**
     * Cập nhật flash sale product nếu startTime > ngày hiện tại
     *
     * @param flashSaleProductId ID của flash sale product cần cập nhật
     * @param request Thông tin cập nhật
     * @throws IllegalArgumentException nếu flash sale đã bắt đầu (startTime <= ngày hiện tại)
     */
    void updateFlashSaleProduct(Long flashSaleProductId, ReqUpdateFlashSaleProductDTO request);

    /**
     * Xóa flash sale product nếu startTime > ngày hiện tại
     *
     * @param flashSaleProductId ID của flash sale product cần xóa
     * @throws IllegalArgumentException nếu flash sale đã bắt đầu (startTime <= ngày hiện tại)
     */
    void deleteFlashSaleProduct(Long flashSaleProductId);

}

