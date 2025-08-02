package com.example.edu.school.user.saga.service;

import com.example.edu.school.user.saga.data.CreateUserData;

public interface UserServiceSaga {


    /**
     * Tạo người dùng mới
     * @param createUserData DTO chứa thông tin người dùng mới
     * @return userId ID của người dùng mới được tạo
     */
    Long saveUser(CreateUserData createUserData);

    /**
     * Xoá người dùng
     * @param userId ID của người dùng cần xoá
     */
    void deleteUser(Long userId);
}
