package com.ecommerce.product.service;

import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.product.dto.ReqCreateCategoryDTO;
import com.ecommerce.product.dto.ReqUpdateCategoryDTO;
import com.ecommerce.product.dto.ReqUpdateCategoryStatusDTO;
import com.ecommerce.product.dto.ResCategoryDTO;
import com.ecommerce.product.dto.ResCategorySearchDTO;
import com.ecommerce.library.enumeration.CategoryStatus;

public interface CategoryService {

    /**
     * Tạo category mới
     *
     * @param request Thông tin category cần tạo
     */
    void createCategory(ReqCreateCategoryDTO request);

    /**
     * Lấy thông tin category theo ID
     *
     * @param categoryId ID của category
     * @return Thông tin category
     */
    ResCategoryDTO getCategoryById(Long categoryId);



    /**
     * Cập nhật thông tin category
     *
     * @param categoryId ID của category cần cập nhật
     * @param request Thông tin cập nhật
     */
    void updateCategory(Long categoryId, ReqUpdateCategoryDTO request);

    /**
     * Cập nhật trạng thái category
     *
     * @param categoryId ID của category cần cập nhật
     * @param request Thông tin trạng thái mới
     */
    void updateCategoryStatus(Long categoryId, ReqUpdateCategoryStatusDTO request);

    /**
     * Tìm kiếm categories theo tên với phân trang
     *
     * @param keyword Từ khóa tìm kiếm
     * @param status Trạng thái category
     * @param pageNo Số trang
     * @param pageSize Kích thước trang
     * @param sortBy Trường sắp xếp
     * @param sortDir Hướng sắp xếp
     * @return Danh sách categories phù hợp
     */
    PageResponse<ResCategorySearchDTO> searchCategories(String keyword, CategoryStatus status,
                                                        int pageNo, int pageSize,
                                                        String sortBy, String sortDir);

}

