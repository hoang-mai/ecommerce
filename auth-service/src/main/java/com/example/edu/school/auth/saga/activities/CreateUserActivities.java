package com.example.edu.school.auth.saga.activities;

import com.example.edu.school.auth.client.dto.ReqCreateUserDTO;
import com.example.edu.school.auth.dto.account.ReqCreateAccountDTO;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CreateUserActivities {

    /**
     * Tạo tài khoản người dùng mới
     * @param reqCreateAccountDTO DTO chứa thông tin tài khoản mới
     * @return accountId ID của tài khoản mới được tạo
     */
    String createAccount(ReqCreateAccountDTO reqCreateAccountDTO);

    /**
     * Xoá tài khoản người dùng
     * @param accountId ID của tài khoản cần xoá
     */
    void deleteAccount(String accountId);

    /**
     * Tạo người dùng mới
     * @param reqCreateUserDTO DTO chứa thông tin người dùng mới
     * @return userId ID của người dùng mới được tạo
     */
    Long createUser(ReqCreateUserDTO reqCreateUserDTO);

    /**
     * Xoá người dùng
     * @param userId ID của người dùng cần xoá
     */
    void deleteUser(Long userId);

}
