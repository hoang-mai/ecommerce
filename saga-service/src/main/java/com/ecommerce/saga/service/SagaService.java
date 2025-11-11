package com.ecommerce.saga.service;

import com.ecommerce.saga.dto.ReqCreateUserDTO;
import jakarta.validation.Valid;

public interface SagaService {
    /**
     * Tạo người dùng mới.
     *
     * @param reqCreateUserDTO DTO chứa thông tin người dùng mới
     */
    void createUser(ReqCreateUserDTO reqCreateUserDTO);

    /**
     * Chấp nhận yêu cầu làm chủ cửa hàng.
     *
     * @param userVerificationId ID của yêu cầu xác minh người dùng
     */
    void approveOwnerRequest(Long userVerificationId);
}
