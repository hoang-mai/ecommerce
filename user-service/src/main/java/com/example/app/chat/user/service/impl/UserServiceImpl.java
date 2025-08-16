package com.example.app.chat.user.service.impl;

import com.example.app.chat.library.exception.NotFoundException;
import com.example.app.chat.library.utils.Constant;
import com.example.app.chat.user.dto.update.UserUpdateRequest;
import com.example.app.chat.user.dto.user.ReqCreateUserDTO;
import com.example.app.chat.user.saga.data.CreateUserData;
import com.example.app.chat.user.saga.workflow.CreateUserWorkFlow;
import com.example.app.chat.user.service.UserService;
import com.example.app.chat.library.utils.PageResponse;
import com.example.app.chat.user.dto.user.UserPreviewResponse;
import com.example.app.chat.user.dto.information.UserResponse;
import com.example.app.chat.library.enumeration.Role;
import com.example.app.chat.user.entity.*;
import com.example.app.chat.user.repository.UserRepository;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final WorkflowClient workflowClient;
    private final UserRepository userRepository;

    @Override
    public void createUser(ReqCreateUserDTO reqCreateUserDTO) {
        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(Constant.CREATE_USER_QUEUE).build();
        CreateUserWorkFlow createUserWorkFlow = workflowClient.newWorkflowStub(CreateUserWorkFlow.class, options);
        WorkflowClient.start(createUserWorkFlow::createUser, CreateUserData.builder()
                .token(((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken().getTokenValue())
                .username(reqCreateUserDTO.getUsername())
                .password(reqCreateUserDTO.getPassword())
                .firstName(reqCreateUserDTO.getFirstName())
                .middleName(reqCreateUserDTO.getMiddleName())
                .lastName(reqCreateUserDTO.getLastName())
                .phoneNumber(reqCreateUserDTO.getPhoneNumber())
                .address(reqCreateUserDTO.getAddress())
                .gender(reqCreateUserDTO.getGender())
                .dateOfBirth(reqCreateUserDTO.getDateOfBirth())
                .build());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public CreateUserData saveUser(CreateUserData createUserData) {
        User user = User.builder()
                .firstName(createUserData.getFirstName())
                .middleName(createUserData.getMiddleName())
                .lastName(createUserData.getLastName())
                .gender(createUserData.getGender())
                .phoneNumber(createUserData.getPhoneNumber())
                .dateOfBirth(createUserData.getDateOfBirth())
                .address(createUserData.getAddress())
                .build();
        userRepository.save(user);
        createUserData.setUserId(user.getUserId());
        return createUserData;
    }


    @Override
    public PageResponse<List<UserPreviewResponse>> getUsers(int page, int size, Role role) {
        return null;
    }


    @Override
    public UserResponse getUserById(Long id) {
        return null;
    }

    @Override
    public void updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Id người dùng không tồn tại"));
        user.setAddress(userUpdateRequest.getAddress());
        user.setAvatarUrl(userUpdateRequest.getAvatarUrl());
        user.setDateOfBirth(userUpdateRequest.getDateOfBirth());
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        user.setGender(userUpdateRequest.getGender());
        userRepository.save(user);
    }

}
 