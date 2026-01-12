package com.ecommerce.read.service.impl;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.user.*;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.AddressDTO;
import com.ecommerce.read.dto.NewUserViewStatisticDTO;
import com.ecommerce.read.entity.UserView;
import com.ecommerce.read.repository.UserViewRepository;
import com.ecommerce.read.repository.impl.UserViewRepositoryImpl;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.UserViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserViewServiceImpl implements UserViewService {
    private final UserViewRepository userViewRepository;
    private final UserViewRepositoryImpl userViewRepositoryImpl;
    private final FileService fileService;

    @Value("${here.url}")
    private String hereUrl;

    @Value("${here.api-key}")
    private String hereApiKey;

    @Override
    public void createUserView(CreateUserEvent createUserEvent) {
        UserView userView = UserView.builder()
            ._id(String.valueOf(createUserEvent.getUserId()))
            .username(createUserEvent.getUsername())
            .email(createUserEvent.getEmail())
            .accountStatus(createUserEvent.getAccountStatus())
            .fullName(createUserEvent.getFullName())
            .phoneNumber(createUserEvent.getPhoneNumber())
            .role(createUserEvent.getRole())
            .createdAt(createUserEvent.getCreatedAt())
            .updatedAt(createUserEvent.getUpdatedAt())
            .build();
        userViewRepository.save(userView);
    }

    @Override
    public void updateRole(UpdateRoleEvent updateRoleEvent) {
        UserView userView = userViewRepository.findById(String.valueOf(updateRoleEvent.getUserId()))
            .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        userView.setRole(updateRoleEvent.getRole());
        userViewRepository.save(userView);
    }

    @Override
    public void updateUserView(UpdateUserEvent updateUserEvent) {
        UserView userView = userViewRepository.findById(String.valueOf(updateUserEvent.getUserId()))
            .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        userView.setFullName(updateUserEvent.getFullName());
        userView.setPhoneNumber(updateUserEvent.getPhoneNumber());
        userView.setEmail(updateUserEvent.getEmail());
        userViewRepository.save(userView);
    }

    @Override
    public void updateAccountStatus(UpdateAccountStatusEvent updateAccountStatusEvent) {
        UserView userView = userViewRepository.findById(String.valueOf(updateAccountStatusEvent.getUserId()))
            .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        userView.setAccountStatus(updateAccountStatusEvent.getAccountStatus());
        userViewRepository.save(userView);
    }

    @Override
    public PageResponse<UserView> getUserViews(AccountStatus accountStatus, Role role, String keyword, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<UserView> userViewPage = userViewRepositoryImpl.getUserView(accountStatus, role, keyword, pageable);

        return PageResponse.<UserView>builder()
            .data(userViewPage.getContent().stream().peek(userView ->
                userView.setAvatarUrl(fileService.getPresignedUrl(userView.getAvatarUrl()))
            ).toList())
            .pageNo(userViewPage.getNumber())
            .pageSize(userViewPage.getSize())
            .totalElements(userViewPage.getTotalElements())
            .totalPages(userViewPage.getTotalPages())
            .build();
    }

    @Override
    public void updateAvatarUser(UpdateAvatarUserEvent updateAvatarUserEvent) {
        UserView userView = userViewRepository.findById(String.valueOf(updateAvatarUserEvent.getUserId()))
            .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        userView.setAvatarUrl(updateAvatarUserEvent.getAvatarUrl());
        userViewRepository.save(userView);
    }

    @Override
    public List<String> searchAddress(String keyword) {
        AddressDTO res = RestClient.builder()
            .baseUrl(hereUrl)
            .build()
            .get()
            .uri(uriBuilder -> uriBuilder
                .queryParam("q", keyword)
                .queryParam("apiKey", hereApiKey)
                .queryParam("lang", "vi")
                .queryParam("limit", 5)
                .queryParam("in", "countryCode:VNM")
                .build())
            .retrieve()
            .body(AddressDTO.class);
        if (res == null || res.getItems() == null) {
            return List.of();
        }

        return res.getItems()
            .stream()
            .map(AddressDTO.Item::getTitle)
            .toList();
    }

    @Override
    public List<NewUserViewStatisticDTO> getUserStatisticsByDateRange(Instant startDate, Instant endDate) {
        return userViewRepositoryImpl.getUserStatisticsByDateRange(startDate, endDate);
    }

}
