package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.auth.ReqLoginDTO;
import com.ecommerce.auth.dto.auth.ReqUpdateAccountDTO;
import com.ecommerce.auth.dto.auth.ResLoginDTO;
import com.ecommerce.library.exception.HttpRequestException;

public interface AuthService {
    /**
     * Đăng nhập vào hệ thống bằng Keycloak
     *
     * @param reqLoginDTO DTO chứa thông tin đăng nhập
     * @return ResLoginDTO chứa thông tin đăng nhập thành công
     * @throws HttpRequestException nếu có lỗi trong quá trình đăng nhập
     */
    ResLoginDTO login(ReqLoginDTO reqLoginDTO);

    /**
     * Cập nhật trạng thái tài khoản người dùng
     *
     * @param reqUpdateAccountDTO DTO chứa thông tin cập nhật trạng thái tài khoản
     */
    void updateAccount(ReqUpdateAccountDTO reqUpdateAccountDTO);
}
