package com.ecommerce.user.saga.activities;

import com.ecommerce.user.saga.data.ApproveOwnerData;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ApproveOwnerActivities {

    /**
     * Cập nhật role của user trong user-service
     *
     * @param approveOwnerData DTO chứa thông tin cập nhật role
     * @return ApproveOwnerData với thông tin đã cập nhật
     */
    ApproveOwnerData updateUserRole(ApproveOwnerData approveOwnerData);

    /**
     * Rollback role của user trong user-service
     *
     * @param approveOwnerData DTO chứa thông tin rollback role
     */
    void rollbackUserRole(ApproveOwnerData approveOwnerData);

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
     * Cập nhật trạng thái verification thành APPROVED
     *
     * @param approveOwnerData DTO chứa thông tin verification
     */
    void updateVerificationStatus(ApproveOwnerData approveOwnerData);

    /**
     * Rollback trạng thái verification về PENDING
     *
     * @param approveOwnerData DTO chứa thông tin verification
     */
    void rollbackVerificationStatus(ApproveOwnerData approveOwnerData);
}

