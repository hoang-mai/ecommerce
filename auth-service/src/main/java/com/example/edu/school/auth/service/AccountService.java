package com.example.edu.school.auth.service;

import com.example.edu.school.auth.dto.account.ReqCreateAccountDTO;

public interface AccountService {
    /**
     * Tạo tài khoản mới.
     *
     * @param reqCreateAccountDTO DTO chứa thông tin tài khoản mới
     */
    String createAccount(ReqCreateAccountDTO reqCreateAccountDTO);

    /**
     * Xoá tài khoản theo ID.
     *
     * @param accountId ID của tài khoản cần xoá
     */
    void deleteAccount(String accountId);
}
