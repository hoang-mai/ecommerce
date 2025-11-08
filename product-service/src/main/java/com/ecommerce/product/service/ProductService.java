package com.ecommerce.product.service;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.ReqCreateProductDTO;
import com.ecommerce.product.dto.ReqUpdateProductDTO;
import com.ecommerce.product.dto.ResProductDTO;

public interface ProductService {

    /**
     * Tạo sản phẩm mới
     *
     * @param request Thông tin sản phẩm cần tạo
     */
    void createProduct(ReqCreateProductDTO request);

    /**
     * Lấy thông tin sản phẩm theo ID
     *
     * @param productId ID của sản phẩm
     * @return Thông tin sản phẩm
     */
    ResProductDTO getProductById(Long productId);

    /**
     * Cập nhật thông tin sản phẩm
     *
     * @param productId ID của sản phẩm cần cập nhật
     * @param request Thông tin cập nhật
     */
    void updateProduct(Long productId, ReqUpdateProductDTO request);

    /**
     * Tìm kiếm sản phẩm với nhiều bộ lọc
     *
     * @param shopId ID của shop (optional)
     * @param categoryId ID của category (optional)
     * @param status Trạng thái sản phẩm (optional)
     * @param keyword Từ khóa tìm kiếm (optional)
     * @param pageNo Số trang
     * @param pageSize Kích thước trang
     * @param sortBy Trường sắp xếp
     * @param sortDir Hướng sắp xếp
     * @return Danh sách sản phẩm phù hợp
     */
    PageResponse<ResProductDTO> searchProducts(Long shopId, Long categoryId, ProductStatus status,
                                                String keyword, int pageNo, int pageSize,
                                                String sortBy, String sortDir);

}

