package com.example.edu.school.auth.service;

import com.example.edu.school.auth.dto.auth.ReqLoginDTO;
import com.example.edu.school.auth.dto.auth.ResLoginDTO;
import com.example.edu.school.library.exception.HttpRequestException;

public interface AuthService {
    /**
     * Đăng nhập vào hệ thống bằng Keycloak
     *
     * @param reqLoginDTO DTO chứa thông tin đăng nhập
     * @return ResLoginDTO chứa thông tin đăng nhập thành công
     * @throws HttpRequestException nếu có lỗi trong quá trình đăng nhập
     */
    ResLoginDTO login(ReqLoginDTO reqLoginDTO);
}
