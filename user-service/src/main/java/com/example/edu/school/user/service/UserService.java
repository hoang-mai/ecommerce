package com.example.edu.school.user.service;

import com.example.edu.school.user.dto.request.update.ParentRelationshipUpdateRequest;
import com.example.edu.school.user.dto.request.update.UserUpdateRequest;
import com.example.edu.school.user.dto.request.register.ParentRegisterRequest;
import com.example.edu.school.user.dto.request.register.RegisterRequest;
import com.example.edu.school.user.dto.response.PageResponse;
import com.example.edu.school.user.dto.response.UserCredentialResponse;
import com.example.edu.school.user.dto.response.UserPreviewResponse;
import com.example.edu.school.user.dto.response.information.UserResponse;
import com.example.edu.school.user.model.Role;

import java.util.List;


public interface UserService {

    UserCredentialResponse getUserCredentialByEmail(String email);

    UserResponse getUserById(Long id);

    void updateUser(Long userId, UserUpdateRequest userUpdateRequest);

    String registerParent(ParentRegisterRequest registerRequest, Long studentId);

    String register(RegisterRequest registerRequest);

    void updateParentRelationship(ParentRelationshipUpdateRequest parentRelationshipUpdateRequest);

    PageResponse<List<UserPreviewResponse>> searchUsers(int page, int size, String query);

    PageResponse<List<UserPreviewResponse>> getUsers(int page, int size, Role role);
}
