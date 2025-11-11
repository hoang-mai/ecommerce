package com.ecommerce.auth.service;

import com.ecommerce.auth.ReqCreateAccountDTO;
import com.ecommerce.auth.dto.auth.*;
import com.ecommerce.library.enumeration.Role;
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

    /**
     * Đăng xuất khỏi hệ thống
     */
    void logout();

    /**
     * Cập nhật trạng thái tài khoản người dùng bởi admin
     *
     * @param reqUpdateAccountDTO DTO chứa thông tin cập nhật trạng thái tài khoản
     * @param userId ID của người dùng cần cập nhật
     */
    void adminUpdateAccountStatus(ReqUpdateAccountDTO reqUpdateAccountDTO, Long userId);

    /**
     * Tạo tài khoản trong hệ thống
     *
     * @param reqCreateAccountDTO DTO chứa thông tin tài khoản mới
     * @param accountId ID của tài khoản cần tạo
     */
    void createAccount(ReqCreateAccountDTO reqCreateAccountDTO, String accountId);

    void deleteAccount(String accountId);

    void updateRole(long userId, Role role);

    /**
     * Làm mới token truy cập
     * @param reqRefreshTokenDTO DTO chứa thông tin làm mới token
     * @return ResLoginDTO chứa token truy cập mới
     */
    ResRefreshTokenDTO refreshToken(ReqRefreshTokenDTO reqRefreshTokenDTO);
}
