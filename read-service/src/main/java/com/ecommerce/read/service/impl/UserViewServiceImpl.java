package com.ecommerce.read.service.impl;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.*;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.UserViewDto;
import com.ecommerce.read.entity.UserView;
import com.ecommerce.read.repository.UserViewRepository;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.UserViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserViewServiceImpl implements UserViewService {
    private  final UserViewRepository userViewRepository;
    private final FileService fileService;

    @Override
    public void createUserView(CreateUserEvent createUserEvent) {
        UserView userView = UserView.builder()
                .userId(createUserEvent.getUserId())
                .username(createUserEvent.getUsername())
                .email(createUserEvent.getEmail())
                .accountStatus(createUserEvent.getAccountStatus())
                .firstName(createUserEvent.getFirstName())
                .middleName(createUserEvent.getMiddleName())
                .lastName(createUserEvent.getLastName())
                .phoneNumber(createUserEvent.getPhoneNumber())
                .role(createUserEvent.getRole())
                .build();
        userViewRepository.save(userView);
    }

    @Override
    public void updateRole(UpdateRoleEvent updateRoleEvent) {
        UserView userView = userViewRepository.findById(updateRoleEvent.getUserId())
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        userView.setRole(updateRoleEvent.getRole());
        userViewRepository.save(userView);
    }

    @Override
    public void updateUserView(UpdateUserEvent updateUserEvent) {
        UserView userView = userViewRepository.findById(updateUserEvent.getUserId())
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        userView.setFirstName(updateUserEvent.getFirstName());
        userView.setMiddleName(updateUserEvent.getMiddleName());
        userView.setLastName(updateUserEvent.getLastName());
        userViewRepository.save(userView);
    }

    @Override
    public void updateAccountStatus(UpdateAccountStatusEvent updateAccountStatusEvent) {
        UserView userView = userViewRepository.findById(updateAccountStatusEvent.getUserId())
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        userView.setAccountStatus(updateAccountStatusEvent.getAccountStatus());
        userViewRepository.save(userView);
    }

    @Override
    public PageResponse<UserViewDto> getUserViews(AccountStatus accountStatus, Role role, String keyword, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<UserView> userViewPage = userViewRepository.getUserView(accountStatus, role, keyword, pageable);

        return PageResponse.<UserViewDto>builder()
                .data(userViewPage.getContent().stream().map(this::mapToDto).toList())
                .pageNo(userViewPage.getNumber())
                .pageSize(userViewPage.getSize())
                .totalElements(userViewPage.getTotalElements())
                .totalPages(userViewPage.getTotalPages())
                .build();
    }

    @Override
    public void updateAvatarUser(UpdateAvatarUserEvent updateAvatarUserEvent) {
        UserView userView = userViewRepository.findById(updateAvatarUserEvent.getUserId())
                .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        userView.setAvatarUrl(updateAvatarUserEvent.getAvatarUrl());
        userViewRepository.save(userView);
    }

    private UserViewDto mapToDto(UserView userView) {
        return UserViewDto.builder()
                .userId(userView.getUserId())
                .username(userView.getUsername())
                .email(userView.getEmail())
                .accountStatus(userView.getAccountStatus())
                .firstName(userView.getFirstName())
                .middleName(userView.getMiddleName())
                .lastName(userView.getLastName())
                .phoneNumber(userView.getPhoneNumber())
                .avatarUrl(fileService.getPresignedUrl(userView.getAvatarUrl()))
                .role(userView.getRole())
                .createdAt(userView.getCreatedAt())
                .updatedAt(userView.getUpdatedAt())
                .build();
    }
}
