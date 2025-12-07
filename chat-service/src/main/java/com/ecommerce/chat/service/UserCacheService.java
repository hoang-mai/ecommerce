package com.ecommerce.chat.service;

import com.ecommerce.chat.entity.UserCache;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.kafka.event.user.CreateUserEvent;
import com.ecommerce.library.kafka.event.user.UpdateUserEvent;

public interface UserCacheService {
    void updateAvatarUrl(Long userId, String avatarUrl);

    void createUserCache(CreateUserEvent createUserEvent);

    void updateUserCache(UpdateUserEvent updateUserEvent);

    void updateAccountStatus(Long userId, AccountStatus accountStatus);
}
