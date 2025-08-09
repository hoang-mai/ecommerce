package com.example.edu.school.auth.service;

import com.example.edu.school.auth.ReqCreateAccountDTO;
import com.example.edu.school.auth.dto.auth.ReqLoginDTO;
import com.example.edu.school.auth.dto.keycloak.ResKeycloakLoginDTO;
import com.example.edu.school.library.exception.DuplicateException;

public interface KeyCloakService {

    /**
     * Đăng nhập vào Keycloak và lấy thông tin đăng nhập
     *
     * @param reqLoginDTO DTO chứa thông tin đăng nhập
     * @return ResKeycloakLoginDTO chứa thông tin đăng nhập thành công
     */
    ResKeycloakLoginDTO login(ReqLoginDTO reqLoginDTO);

    /**
     * Đăng ký tài khoản mới trong Keycloak
     *
     * @param reqCreateAccountDTO DTO chứa thông tin tài khoản mới
     * @throws DuplicateException nếu email đã tồn tại
     */
    String register(ReqCreateAccountDTO reqCreateAccountDTO);

    void delete(String accountId);
}
