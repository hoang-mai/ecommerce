package com.ecommerce.product.service;

import com.ecommerce.product.dto.ReqCreateProductAttributeDTO;
import com.ecommerce.product.dto.ReqUpdateProductAttributeDTO;
import com.ecommerce.product.dto.ResProductAttributeDTO;

import java.util.List;

public interface ProductAttributeService {

    /**
     * Tạo mới một product attribute với các giá trị của nó
     *
     * @param request Request DTO chứa thông tin product attribute
     */
    void createProductAttribute(ReqCreateProductAttributeDTO request);

    /**
     * Lấy thông tin product attribute theo ID
     *
     * @param attributeId ID của product attribute
     * @return ResProductAttributeDTO
     */
    ResProductAttributeDTO getProductAttributeById(Long attributeId);

    /**
     * Lấy danh sách product attributes theo product ID
     *
     * @param productId ID của product
     * @return List of ResProductAttributeDTO
     */
    List<ResProductAttributeDTO> getProductAttributesByProductId(Long productId);

    /**
     * Cập nhật thông tin product attribute
     *
     * @param attributeId ID của product attribute cần cập nhật
     * @param request Request DTO chứa thông tin cập nhật
     * @return ResProductAttributeDTO
     */
    ResProductAttributeDTO updateProductAttribute(Long attributeId, ReqUpdateProductAttributeDTO request);

    /**
     * Xóa product attribute
     *
     * @param attributeId ID của product attribute cần xóa
     */
    void deleteProductAttribute(Long attributeId);
}
