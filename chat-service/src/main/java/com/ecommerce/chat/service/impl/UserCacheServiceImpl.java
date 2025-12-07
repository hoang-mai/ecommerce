package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.entity.UserCache;
import com.ecommerce.chat.repository.UserCacheRepository;
import com.ecommerce.chat.service.UserCacheService;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.user.CreateUserEvent;
import com.ecommerce.library.kafka.event.user.UpdateUserEvent;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheServiceImpl implements UserCacheService {

    private final UserCacheRepository userCacheRepository;

    @Override
    public void createUserCache(CreateUserEvent createUserEvent) {

        UserCache userCache = UserCache.builder()
            ._id(String.valueOf(createUserEvent.getUserId()))
            .email(createUserEvent.getEmail())
            .accountStatus(createUserEvent.getAccountStatus())
            .fullName(createUserEvent.getFullName())
            .phoneNumber(createUserEvent.getPhoneNumber())
            .createdAt(createUserEvent.getCreatedAt())
            .updatedAt(createUserEvent.getUpdatedAt())
            .build();
        userCacheRepository.save(userCache);
    }

    @Override
    public void updateUserCache(UpdateUserEvent updateUserEvent) {

        UserCache userCache = userCacheRepository.findById(String.valueOf(updateUserEvent.getUserId()))
            .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));

        userCache.setFullName(updateUserEvent.getFullName());
        userCache.setPhoneNumber(updateUserEvent.getPhoneNumber());
        userCache.setEmail(updateUserEvent.getEmail());

        userCacheRepository.save(userCache);
    }

    @Override
    public void updateAccountStatus(Long userId, AccountStatus accountStatus) {

        UserCache userCache = userCacheRepository.findById(userId.toString())
            .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        userCache.setAccountStatus(accountStatus);
    }

    @Override
    public void updateAvatarUrl(Long userId, String avatarUrl) {

        UserCache userCache = userCacheRepository.findById(userId.toString())
            .orElseThrow(() -> new NotFoundException(MessageError.USER_NOT_FOUND));
        userCache.setAvatarUrl(avatarUrl);

    }

}

