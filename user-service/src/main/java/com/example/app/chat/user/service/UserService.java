package com.example.app.chat.user.service;

import com.example.app.chat.user.dto.update.UserUpdateRequest;
import com.example.app.chat.user.dto.user.ReqCreateUserDTO;
import com.example.app.chat.library.utils.PageResponse;
import com.example.app.chat.user.dto.user.UserPreviewResponse;
import com.example.app.chat.user.dto.information.UserResponse;
import com.example.app.chat.library.enumeration.Role;
import com.example.app.chat.user.saga.data.CreateUserData;

import java.util.List;


public interface UserService {

    UserResponse getUserById(Long id);

    void updateUser(Long userId, UserUpdateRequest userUpdateRequest);


    PageResponse<List<UserPreviewResponse>> getUsers(int page, int size, Role role);

    /**
     * Tạo người dùng mới.
     *
     * @param reqCreateUserDTO DTO chứa thông tin người dùng mới
     */
    void createUser(ReqCreateUserDTO reqCreateUserDTO);

    /**
     * Xoá người dùng theo ID.
     *
     * @param userId ID của người dùng cần xoá
     */
    void deleteUser(Long userId);

    /**
     * Tạo người dùng mới với thông tin từ CreateUserData.
     *
     * @param createUserData DTO chứa thông tin người dùng mới
     * @return CreateUserData chứa thông tin người dùng mới đã được tạo
     */
    CreateUserData saveUser(CreateUserData createUserData);


}
