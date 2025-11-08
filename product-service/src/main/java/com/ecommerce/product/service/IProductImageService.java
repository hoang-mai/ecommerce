package com.ecommerce.product.service;

import com.ecommerce.product.dto.ReqCreateProductImageDTO;
import com.ecommerce.product.dto.ReqUpdateProductImageDTO;
import com.ecommerce.product.dto.ResProductImageDTO;

import java.util.List;

public interface IProductImageService {

    /**
     * Tạo mới một product image
     *
     * @param request Request DTO chứa thông tin product image
     * @return ResProductImageDTO
     */
    ResProductImageDTO createProductImage(ReqCreateProductImageDTO request);

    /**
     * Lấy thông tin product image theo ID
     *
     * @param imageId ID của product image
     * @return ResProductImageDTO
     */
    ResProductImageDTO getProductImageById(Long imageId);

    /**
     * Lấy danh sách product images theo product ID
     *
     * @param productId ID của product
     * @return List of ResProductImageDTO
     */
    List<ResProductImageDTO> getProductImagesByProductId(Long productId);

    /**
     * Cập nhật thông tin product image
     *
     * @param imageId ID của product image cần cập nhật
     * @param request Request DTO chứa thông tin cập nhật
     * @return ResProductImageDTO
     */
    ResProductImageDTO updateProductImage(Long imageId, ReqUpdateProductImageDTO request);

    /**
     * Xóa product image
     *
     * @param imageId ID của product image cần xóa
     */
    void deleteProductImage(Long imageId);
}

