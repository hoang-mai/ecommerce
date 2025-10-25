package com.ecommerce.user.service;

import com.ecommerce.user.dto.ReqUpdateUserDTO;
import com.ecommerce.user.dto.ResInfoPreviewUserDTO;
import com.ecommerce.user.dto.ReqCreateUserDTO;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.user.dto.ResInfoUserDTO;
import com.ecommerce.user.saga.data.CreateUserData;


public interface UserService {
    /**
     * Tạo người dùng mới.
     *
     * @param reqCreateUserDTO DTO chứa thông tin người dùng mới
     */
    void createUser(ReqCreateUserDTO reqCreateUserDTO);

    /**
     * Tạo người dùng mới với thông tin từ CreateUserData.
     *
     * @param createUserData DTO chứa thông tin người dùng mới
     * @return CreateUserData chứa thông tin người dùng mới đã được tạo
     */
    CreateUserData saveUser(CreateUserData createUserData);

    /**
     * Lấy thông tin người dùng theo ID.
     *
     * @param id ID của người dùng
     * @return Thông tin người dùng
     */
    ResInfoUserDTO getUserById(Long id);


    /**
     * Cập nhật thông tin người dùng theo ID.
     *
     * @param reqUpdateUserDTO DTO chứa thông tin cập nhật người dùng
     */
    void updateUser(ReqUpdateUserDTO reqUpdateUserDTO);

    /**
     * Xoá người dùng theo ID.
     *
     * @param userId ID của người dùng cần xoá
     */
    void deleteUser(Long userId);


    /**
     * Tìm kiếm người dùng theo tên  hoặc email.
     *
     * @param pageNo  Số trang
     * @param pageSize Kích thước trang
     * @param query   Chuỗi tìm kiếm (tên  hoặc email)
     * @return Danh sách người dùng phù hợp với trang và kích thước trang
     */
    PageResponse<ResInfoPreviewUserDTO> searchUsers(int pageNo, int pageSize, String query);

    /**
     * Lấy thông tin người dùng hiện tại theo ID.
     *
     * @return Thông tin người dùng hiện tại
     */
    ResInfoUserDTO getCurrentUser();
}
