package com.ecommerce.user.service.impl;

import com.ecommerce.library.kafka.event.user.UpdateAvatarUserEvent;
import com.ecommerce.library.kafka.event.user.UpdateUserEvent;
import com.ecommerce.user.ReqCreateUserDTO;
import com.ecommerce.user.ReqRollbackUpdateUserRoleAndVerificationStatusDTO;
import com.ecommerce.user.ReqUpdateUserRoleAndVerificationStatusDTO;
import com.ecommerce.user.ResUpdateUserRoleAndVerificationStatusDTO;
import com.ecommerce.user.dto.*;
import com.ecommerce.user.entity.User;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.DuplicateException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.enumeration.UserVerificationStatus;
import com.ecommerce.user.messaging.producer.UserEventProducer;
import com.ecommerce.user.repository.UserVerificationRepository;
import com.ecommerce.user.service.FileService;
import com.ecommerce.user.service.UserService;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.user.entity.*;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserVerificationRepository userVerificationRepository;
    private final UserHelper userHelper;
    private final FileService fileService;
    private final UserEventProducer userEventProducer;


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
    @Transactional
    @Override
    public void uploadAvatar(MultipartFile file, Boolean isDelete) {
        Long currentUserId = userHelper.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        if (FnCommon.isNotNull(isDelete) && isDelete && file == null) {
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
        userEventProducer.send(
                UpdateAvatarUserEvent.builder()
                        .userId(user.getUserId())
                        .avatarUrl(avatarUrl)
                        .build()
        );
    }

    @Transactional
    @Override
    public void rollbackUpdateUserRoleAndVerificationStatus(ReqRollbackUpdateUserRoleAndVerificationStatusDTO reqRollbackUpdateUserRoleAndVerificationStatusDTO) {
        UserVerification userVerification = userVerificationRepository.findById(reqRollbackUpdateUserRoleAndVerificationStatusDTO.getUserVerificationId())
                .orElseThrow(() -> new NotFoundException(MessageError.USER_VERIFICATION_NOT_FOUND));
        User user = userVerification.getUser();
        user.setRole(FnCommon.convertRoleProtoToRole(reqRollbackUpdateUserRoleAndVerificationStatusDTO.getOldRole()));
        userVerification.setUserVerificationStatus(FnCommon.convertUserVerificationStatusProtoToJava(reqRollbackUpdateUserRoleAndVerificationStatusDTO.getOldVerificationStatus()));
        userRepository.save(user);
        userVerificationRepository.save(userVerification);
    }

    @Transactional
    @Override
    public ResUpdateUserRoleAndVerificationStatusDTO updateUserRoleAndVerificationStatus(ReqUpdateUserRoleAndVerificationStatusDTO reqUpdateUserRoleAndVerificationStatusDTO) {
        UserVerification userVerification = userVerificationRepository.findById(reqUpdateUserRoleAndVerificationStatusDTO.getUserVerificationId())
                .orElseThrow(() -> new NotFoundException(MessageError.USER_VERIFICATION_NOT_FOUND));
        User user = userVerification.getUser();
        Role oldRole = user.getRole();
        UserVerificationStatus oldStatus = userVerification.getUserVerificationStatus();

        user.setRole(FnCommon.convertRoleProtoToRole(reqUpdateUserRoleAndVerificationStatusDTO.getNewRole()));
        userVerification.setUserVerificationStatus(FnCommon.convertUserVerificationStatusProtoToJava(reqUpdateUserRoleAndVerificationStatusDTO.getNewVerificationStatus()));

        userRepository.save(user);
        userVerificationRepository.save(userVerification);
        return ResUpdateUserRoleAndVerificationStatusDTO.newBuilder()
                .setUserId(user.getUserId())
                .setOldRole(FnCommon.convertRoleToRoleProto(oldRole))
                .setOldVerificationStatus(FnCommon.convertUserVerificationStatusToProto(oldStatus))
                .build();
    }


    @Override
    public Long createUser(ReqCreateUserDTO reqCreateUserDTO) {
        User user = User.builder()
                .firstName(reqCreateUserDTO.getFirstName())
                .middleName(reqCreateUserDTO.getMiddleName())
                .lastName(reqCreateUserDTO.getLastName())
                .role(FnCommon.convertRoleProtoToRole(reqCreateUserDTO.getRole()))
                .gender(FnCommon.convertGenderProtoToGender(reqCreateUserDTO.getGender()))
                .build();
        Address address = Address.builder()
                .user(user)
                .province(reqCreateUserDTO.getProvince())
                .ward(reqCreateUserDTO.getWard())
                .detail(reqCreateUserDTO.getDetail())
                .receiverName(reqCreateUserDTO.getReceiverName())
                .phoneNumber(reqCreateUserDTO.getPhoneNumber())
                .isDefault(reqCreateUserDTO.getIsDefault())
                .build();
        user.setAddresses(List.of(address));
        userRepository.save(user);
        return user.getUserId();
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
        userEventProducer.send(UpdateUserEvent.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build());
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
 