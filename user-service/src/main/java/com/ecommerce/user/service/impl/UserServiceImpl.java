package com.ecommerce.user.service.impl;

import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.user.dto.*;
import com.ecommerce.user.entity.User;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.DuplicateException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.user.enumeration.UserVerificationStatus;
import com.ecommerce.user.repository.AddressRepository;
import com.ecommerce.user.repository.UserVerificationRepository;
import com.ecommerce.user.saga.data.ApproveOwnerData;
import com.ecommerce.user.saga.data.CreateUserData;
import com.ecommerce.user.saga.workflow.CreateUserWorkFlow;
import com.ecommerce.user.service.FileService;
import com.ecommerce.user.service.UserService;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.user.entity.*;
import com.ecommerce.user.repository.UserRepository;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final WorkflowClient workflowClient;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserVerificationRepository userVerificationRepository;
    private final UserHelper userHelper;
    private final FileService fileService;

    @Override
    public void createUser(ReqCreateUserDTO reqCreateUserDTO) {
        try{
            WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(Constant.CREATE_USER_QUEUE).build();
            CreateUserWorkFlow createUserWorkFlow = workflowClient.newWorkflowStub(CreateUserWorkFlow.class, options);
            createUserWorkFlow.createUser(CreateUserData.builder()
                    .username(reqCreateUserDTO.getUsername())
                    .password(reqCreateUserDTO.getPassword())
                    .firstName(reqCreateUserDTO.getFirstName())
                    .middleName(reqCreateUserDTO.getMiddleName())
                    .lastName(reqCreateUserDTO.getLastName())
                    .phoneNumber(reqCreateUserDTO.getPhoneNumber())
                    .gender(reqCreateUserDTO.getGender())
                    .ward(reqCreateUserDTO.getWard())
                    .province(reqCreateUserDTO.getProvince())
                    .detail(reqCreateUserDTO.getDetail())
                    .receiverName(reqCreateUserDTO.getReceiverName())
                    .build());
        }catch (WorkflowFailedException e){
            if (e.getCause().getCause() instanceof ApplicationFailure failure) {
                throw new HttpRequestException(failure.getOriginalMessage(), HttpStatus.BAD_REQUEST.value(),LocalDateTime.now());

            }
            throw new HttpRequestException(e.getCause().getCause().getLocalizedMessage(), HttpStatus.BAD_REQUEST.value(),LocalDateTime.now());
        }

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
    public void uploadAvatar(MultipartFile file, Boolean isDelete) {
        Long currentUserId = userHelper.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        if(FnCommon.isNotNull(isDelete) && isDelete && file == null){
            user.setAvatarUrl(null);
            fileService.deleteFilesInDirectory("avatars/" + currentUserId);
            userRepository.save(user);
            return;
        }
        if (file.getSize() > 3 * 1024 * 1024) {
            throw new IllegalArgumentException(MessageError.FILE_SIZE_EXCEEDED);
        }
        String avatarUrl = fileService.uploadFile(file, "avatars/" + currentUserId);
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
    }

    @Override
    public Role updateUserRole(ApproveOwnerData approveOwnerData) {
        User user = userRepository.findById(approveOwnerData.getUserId())
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        Role oldRole = user.getRole();
        user.setRole(Role.OWNER);
        userRepository.save(user);
        return oldRole;
    }

    @Override
    public void rollbackUserRole(ApproveOwnerData approveOwnerData) {
        User user = userRepository.findById(approveOwnerData.getUserId())
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        user.setRole(approveOwnerData.getOldRole());
        userRepository.save(user);
    }


    @Override
    public CreateUserData saveUser(CreateUserData createUserData) {
        User user = User.builder()
                .firstName(createUserData.getFirstName())
                .middleName(createUserData.getMiddleName())
                .lastName(createUserData.getLastName())
                .role(Role.USER)
                .gender(createUserData.getGender())
                .build();
        userRepository.save(user);
        Address address = Address.builder()
                .user(user)
                .province(createUserData.getProvince())
                .ward(createUserData.getWard())
                .detail(createUserData.getDetail())
                .receiverName(createUserData.getReceiverName())
                .phoneNumber(createUserData.getPhoneNumber())
                .isDefault(true)
                .build();
        addressRepository.save(address);
        createUserData.setUserId(user.getUserId());
        return createUserData;
    }


    @Override
    public void updateUser(ReqUpdateUserDTO reqUpdateUserDTO) {
        Long userId = userHelper.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));

        if (FnCommon.isNotNull(reqUpdateUserDTO.getEmail())) {
            if (userRepository.existsEmailAlreadyUsedByAnotherUser(userId, reqUpdateUserDTO.getEmail()))
                throw new DuplicateException(MessageError.EMAIL_EXISTS);
        }
        user.setEmail(reqUpdateUserDTO.getEmail());
        user.setDescription(reqUpdateUserDTO.getDescription());
        user.setFirstName(reqUpdateUserDTO.getFirstName());
        user.setMiddleName(reqUpdateUserDTO.getMiddleName());
        user.setLastName(reqUpdateUserDTO.getLastName());
        user.setPhoneNumber(reqUpdateUserDTO.getPhoneNumber());
        user.setGender(reqUpdateUserDTO.getGender());
        user.setDateOfBirth(reqUpdateUserDTO.getDateOfBirth());

        userRepository.save(user);
    }


    @Override
    public ResInfoUserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        Boolean isVerified = userVerificationRepository.existsByUserAndUserVerificationStatusNot(user, UserVerificationStatus.REJECTED);
        return ResInfoUserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .description(user.getDescription())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .avatarUrl(fileService.getPresignedUrl(user.getAvatarUrl()))
                .role(user.getRole())
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isVerification(isVerified)
                .build();
    }

}
 