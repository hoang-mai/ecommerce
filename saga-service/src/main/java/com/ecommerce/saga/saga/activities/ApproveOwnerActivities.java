package com.ecommerce.saga.saga.activities;

import com.ecommerce.saga.saga.data.ApproveOwnerData;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ApproveOwnerActivities {

    /**
     * Cập nhật role của user trong user-service và verification status thành APPROVED
     *
     * @param approveOwnerData DTO chứa thông tin cập nhật role và trạng thái xác minh
     * @return ApproveOwnerData chứa thông tin đã được cập nhật
     */
    ApproveOwnerData updateUserRoleAndVerificationStatus(ApproveOwnerData approveOwnerData);

    /**
     * Rollback role của user trong user-service
     *
     * @param approveOwnerData DTO chứa thông tin rollback role
     */
    void rollbackUserRoleAndVerificationStatus(ApproveOwnerData approveOwnerData);

    /**
     * Cập nhật role của account trong auth-service
     *
     * @param approveOwnerData DTO chứa thông tin cập nhật role
     */
    void updateAccountRole(ApproveOwnerData approveOwnerData);

    /**
     * Rollback role của account trong auth-service
     *
     * @param approveOwnerData DTO chứa thông tin rollback role
     */
    void rollbackAccountRole(ApproveOwnerData approveOwnerData);

    /**
     * Đồng bộ cập nhật role sang read-service
     *
     * @param approveOwnerData DTO chứa thông tin cập nhật role
     */
    void updateUserRole(ApproveOwnerData approveOwnerData);
}

