package com.example.edu.school.user.service;

import com.example.edu.school.user.dto.update.ParentRelationshipUpdateRequest;
import com.example.edu.school.user.dto.update.UserUpdateRequest;
import com.example.edu.school.user.dto.user.ParentReqCreateUserDTO;
import com.example.edu.school.user.dto.user.ReqCreateUserDTO;
import com.example.edu.school.library.utils.PageResponse;
import com.example.edu.school.user.dto.user.UserPreviewResponse;
import com.example.edu.school.user.dto.information.UserResponse;
import com.example.edu.school.library.enumeration.Role;
import com.example.edu.school.user.saga.data.CreateUserData;
import jakarta.validation.Valid;

import java.util.List;


public interface UserService {

    UserResponse getUserById(Long id);

    void updateUser(Long userId, UserUpdateRequest userUpdateRequest);




    PageResponse<List<UserPreviewResponse>> getUsers(int page, int size, Role role);

    /**
     * Tạo người dùng mới.
     * @param reqCreateUserDTO DTO chứa thông tin người dùng mới
     */
    void createUser(ReqCreateUserDTO reqCreateUserDTO);

    /**
     * Xoá người dùng theo ID.
     * @param userId ID của người dùng cần xoá
     */
    void deleteUser(Long userId);

    /**
     * Tạo người dùng mới với thông tin từ CreateUserData.
     * @param createUserData DTO chứa thông tin người dùng mới
     * @return ID của người dùng mới được tạo
     */
    Long saveUser(CreateUserData createUserData);


}
