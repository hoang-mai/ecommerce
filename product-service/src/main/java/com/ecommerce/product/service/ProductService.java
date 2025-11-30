package com.ecommerce.product.service;

import com.ecommerce.library.enumeration.ProductVariantStatus;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    /**
     * Tạo sản phẩm mới
     *
     * @param request Thông tin sản phẩm cần tạo
     */
    void createProduct(ReqCreateProductDTO request, List<MultipartFile> files);


    /**
     * Cập nhật thông tin sản phẩm
     *
     * @param productId ID của sản phẩm cần cập nhật
     * @param request Thông tin cập nhật
     * @param files Danh sách ảnh mới (optional)
     */
    void updateProduct(Long productId, ReqUpdateProductDTO request, List<MultipartFile> files);

    /**
     * Cập nhật trạng thái biến thể sản phẩm
     *
     * @param productVariantId ID của sản phẩm cần cập nhật trạng thái
     * @param request Trạng thái mới
     */
    void updateProductVariantStatus(Long productVariantId, ReqUpdateProductVariantStatusDTO request);

    /**
     * Cập nhật trạng thái sản phẩm theo ID sản phẩm
     * @param productId ID của sản phẩm
     * @param status Trạng thái mới
     */
    void updateProductStatusByProductId(Long productId, ReqUpdateProductStatusDTO status);

    void handleCreateOrderEvent(List<CreateOrderEvent> createOrderEventList);
}

