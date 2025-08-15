package com.example.app.chat.user.saga.service;

import com.example.app.chat.user.saga.data.CreateUserData;

public interface UserServiceSaga {


    /**
     * Tạo người dùng mới
     *
     * @param createUserData DTO chứa thông tin người dùng mới
     * @return CreateUserData chứa thông tin người dùng mới đã được tạo
     */
    CreateUserData saveUser(CreateUserData createUserData);

    /**
     * Xoá người dùng
     *
     * @param userId ID của người dùng cần xoá
     */
    void deleteUser(Long userId);
}
