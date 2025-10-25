package com.ecommerce.user.service.impl;

import com.ecommerce.user.entity.User;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.DuplicateException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.user.dto.ReqUpdateUserDTO;
import com.ecommerce.user.dto.ResInfoPreviewUserDTO;
import com.ecommerce.user.dto.ReqCreateUserDTO;
import com.ecommerce.user.saga.data.CreateUserData;
import com.ecommerce.user.saga.workflow.CreateUserWorkFlow;
import com.ecommerce.user.service.UserService;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.user.dto.ResInfoUserDTO;
import com.ecommerce.library.enumeration.Role;
import com.example.app.chat.user.entity.*;
import com.ecommerce.user.repository.UserRepository;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final WorkflowClient workflowClient;
    private final UserRepository userRepository;
    private final UserHelper userHelper;

    @Override
    public void createUser(ReqCreateUserDTO reqCreateUserDTO) {

        if (userRepository.existsByEmail(reqCreateUserDTO.getEmail())) {
            throw new DuplicateException(MessageError.EMAIL_EXISTS);
        }

        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(Constant.CREATE_USER_QUEUE).build();
        CreateUserWorkFlow createUserWorkFlow = workflowClient.newWorkflowStub(CreateUserWorkFlow.class, options);
        WorkflowClient.start(createUserWorkFlow::createUser, CreateUserData.builder()
                .username(reqCreateUserDTO.getUsername())
                .password(reqCreateUserDTO.getPassword())
                .email(reqCreateUserDTO.getEmail())
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
    public PageResponse<ResInfoPreviewUserDTO> searchUsers(int pageNo, int pageSize, String query) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ResInfoPreviewUserDTO> listPage = userRepository.searchUsersByNameOrEmail(query, pageable);
        return PageResponse.<ResInfoPreviewUserDTO>builder()
                .pageNo(listPage.getNumber())
                .pageSize(listPage.getSize())
                .totalPages(listPage.getTotalPages())
                .totalElements(listPage.getTotalElements())
                .hasNextPage(listPage.hasNext())
                .hasPreviousPage(listPage.hasPrevious())
                .data(listPage.getContent())
                .build();
    }

    @Override
    public ResInfoUserDTO getCurrentUser() {
        Long currentUserId = userHelper.getCurrentUserId();
        return getUserById(currentUserId);
    }

    @Override
    public CreateUserData saveUser(CreateUserData createUserData) {
        User user = User.builder()
                .email(createUserData.getEmail())
                .firstName(createUserData.getFirstName())
                .middleName(createUserData.getMiddleName())
                .lastName(createUserData.getLastName())
                .gender(createUserData.getGender())
                .phoneNumber(createUserData.getPhoneNumber())
                .dateOfBirth(createUserData.getDateOfBirth())
                .address(createUserData.getAddress())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        createUserData.setUserId(user.getUserId());
        return createUserData;
    }


    @Override
    public void updateUser(ReqUpdateUserDTO reqUpdateUserDTO) {
        Long userId = userHelper.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));

        if (FnCommon.isNotNull(reqUpdateUserDTO.getEmail())) {
            if(userRepository.existsEmailAlreadyUsedByAnotherUser(userId, reqUpdateUserDTO.getEmail()))throw new DuplicateException(MessageError.EMAIL_EXISTS);
            else user.setEmail(reqUpdateUserDTO.getEmail());
        }
        if(FnCommon.isNotNull(reqUpdateUserDTO.getDescription())) {
            user.setDescription(reqUpdateUserDTO.getDescription());
        }
        if(FnCommon.isNotNull(reqUpdateUserDTO.getFirstName())) {
            user.setFirstName(reqUpdateUserDTO.getFirstName());
        }
        if(FnCommon.isNotNull(reqUpdateUserDTO.getMiddleName())) {
            user.setMiddleName(reqUpdateUserDTO.getMiddleName());
        }
        if(FnCommon.isNotNull(reqUpdateUserDTO.getLastName())) {
            user.setLastName(reqUpdateUserDTO.getLastName());
        }
        if(FnCommon.isNotNull(reqUpdateUserDTO.getPhoneNumber())) {
            user.setPhoneNumber(reqUpdateUserDTO.getPhoneNumber());
        }
        if(FnCommon.isNotNull(reqUpdateUserDTO.getAddress())) {
            user.setAddress(reqUpdateUserDTO.getAddress());
        }
        if(FnCommon.isNotNull(reqUpdateUserDTO.getGender())) {
            user.setGender(reqUpdateUserDTO.getGender());
        }
        if(FnCommon.isNotNull(reqUpdateUserDTO.getDateOfBirth())) {
            user.setDateOfBirth(reqUpdateUserDTO.getDateOfBirth());
        }

        userRepository.save(user);
    }


    @Override
    public ResInfoUserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        return ResInfoUserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .description(user.getDescription())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
 