package com.example.edu.school.user.saga.activities;

import com.example.edu.school.user.dto.account.ReqCreateAccountDTO;
import com.example.edu.school.user.dto.user.ReqCreateUserDTO;
import com.example.edu.school.user.saga.data.CreateUserData;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CreateUserActivities {

    /**
     * Tạo tài khoản người dùng mới
     * @param createUserData DTO chứa thông tin tài khoản mới
     * @return accountId ID của tài khoản mới được tạo
     */
    String createAccount(CreateUserData createUserData);

    /**
     * Xoá tài khoản người dùng
     * @param accountId ID của tài khoản cần xoá
     */
    void deleteAccount(String accountId);

    /**
     * Tạo người dùng mới
     * @param createUserData DTO chứa thông tin người dùng mới
     * @return userId ID của người dùng mới được tạo
     */
    Long createUser(CreateUserData createUserData);

    /**
     * Xoá người dùng
     * @param userId ID của người dùng cần xoá
     */
    void deleteUser(Long userId);

}
