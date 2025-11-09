package com.ecommerce.user.saga.service;

import com.ecommerce.library.enumeration.Role;
import com.ecommerce.user.saga.data.ApproveOwnerData;
import com.ecommerce.user.saga.data.CreateUserData;

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

    /**
     * Cập nhật role của người dùng khi duyệt chủ cửa hàng
     * @param  approveOwnerData DTO chứa thông tin cập nhật role
     *
     * @return Role vai trò đã được cập nhật
     */
    Role updateUserRole(ApproveOwnerData approveOwnerData);

    /**
     * Rollback role của người dùng khi duyệt chủ cửa hàng thất bại
     *
     * @param approveOwnerData DTO chứa thông tin rollback role
     */
    void rollbackUserRole(ApproveOwnerData approveOwnerData);
}
