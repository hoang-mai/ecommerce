package com.example.app.chat.user.service;

import com.example.app.chat.user.dto.ReqUpdateUserDTO;
import com.example.app.chat.user.dto.ResInfoPreviewUserDTO;
import com.example.app.chat.user.dto.ReqCreateUserDTO;
import com.example.app.chat.library.utils.PageResponse;
import com.example.app.chat.user.dto.ResInfoUserDTO;
import com.example.app.chat.user.saga.data.CreateUserData;

import java.util.List;


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
     * @param userId           ID của người dùng cần cập nhật
     * @param reqUpdateUserDTO DTO chứa thông tin cập nhật người dùng
     */
    void updateUserById(Long userId, ReqUpdateUserDTO reqUpdateUserDTO);

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
     * @param userId ID của người dùng
     * @return Thông tin người dùng hiện tại
     */
    ResInfoUserDTO getCurrentUserById(Long userId);
}
