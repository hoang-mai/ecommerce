package com.ecommerce.user.saga.activities;

import com.ecommerce.user.saga.data.CreateUserData;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CreateUserActivities {

    /**
     * Tạo tài khoản người dùng mới
     *
     * @param createUserData DTO chứa thông tin tài khoản mới
     * @return CreateUserData chứa accountId thông tin tài khoản đã được tạo
     */
    CreateUserData createAccount(CreateUserData createUserData);

    /**
     * Xoá tài khoản người dùng
     *
     * @param createUserData DTO chứa accountId thông tin tài khoản cần xoá
     */
    void deleteAccount(CreateUserData createUserData);

    /**
     * Tạo người dùng mới
     *
     * @param createUserData DTO chứa thông tin người dùng mới
     * @return CreateUserData chứa thông tin người dùng mới đã được tạo
     */
    CreateUserData createUser(CreateUserData createUserData);

    /**
     * Xoá người dùng
     *
     * @param userId ID của người dùng cần xoá
     */
    void deleteUser(Long userId);

}
