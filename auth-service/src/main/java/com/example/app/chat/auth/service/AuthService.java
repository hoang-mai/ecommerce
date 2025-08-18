package com.example.app.chat.auth.service;

import com.example.app.chat.auth.dto.auth.ReqLoginDTO;
import com.example.app.chat.auth.dto.auth.ReqUpdateAccountDTO;
import com.example.app.chat.auth.dto.auth.ResLoginDTO;
import com.example.app.chat.library.exception.HttpRequestException;

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
