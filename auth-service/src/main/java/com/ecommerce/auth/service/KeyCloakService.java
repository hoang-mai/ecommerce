package com.ecommerce.auth.service;

import com.ecommerce.auth.ReqCreateAccountDTO;
import com.ecommerce.auth.dto.auth.ReqLoginDTO;
import com.ecommerce.auth.dto.auth.ReqUpdateAccountDTO;
import com.ecommerce.auth.dto.keycloak.ResKeycloakLoginDTO;
import com.ecommerce.library.exception.DuplicateException;

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

    /**
     * Xóa tài khoản trong Keycloak
     *
     * @param accountId ID của tài khoản cần xóa
     */
    void delete(String accountId);

    /**
     * Cập nhật thông tin tài khoản trong Keycloak
     *
     * @param reqUpdateStatusAccountDTO DTO chứa thông tin cập nhật trạng thái tài khoản
     */
    void updateAccount(ReqUpdateAccountDTO reqUpdateStatusAccountDTO);

    /**
     * Đăng xuất khỏi Keycloak
     */
    void logout();

    /**
     * Cập nhật trạng thái tài khoản người dùng bởi admin
     *
     * @param reqUpdateAccountDTO DTO chứa thông tin cập nhật trạng thái tài khoản
     * @param accountId ID của người dùng cần cập nhật
     */
    void adminUpdateAccountStatus(ReqUpdateAccountDTO reqUpdateAccountDTO, String accountId);
}
