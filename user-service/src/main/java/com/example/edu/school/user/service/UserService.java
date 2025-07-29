package com.example.edu.school.user.service;

import com.example.edu.school.user.dto.update.ParentRelationshipUpdateRequest;
import com.example.edu.school.user.dto.update.UserUpdateRequest;
import com.example.edu.school.user.dto.user.ParentReqCreateUserDTO;
import com.example.edu.school.user.dto.user.ReqCreateUserDTO;
import com.example.edu.school.library.utils.PageResponse;
import com.example.edu.school.user.dto.user.UserPreviewResponse;
import com.example.edu.school.user.dto.information.UserResponse;
import com.example.edu.school.library.enumeration.Role;
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
    void create(ReqCreateUserDTO reqCreateUserDTO);
}
