package com.ecommerce.read.service;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.kafka.event.*;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.UserViewDto;

public interface UserViewService {

    /**
     * Tạo mới User View khi nhận được sự kiện tạo người dùng
     * @param createUserEvent Sự kiện tạo người dùng
     */
    void createUserView(CreateUserEvent createUserEvent);

    /**
     * Cập nhật vai trò của người dùng trong User View
     * @param updateRoleEvent Vai trò mới của người dùng
     */
    void updateRole(UpdateRoleEvent updateRoleEvent);

    void updateUserView(UpdateUserEvent updateUserEvent);

    /**
     * Cập nhật trạng thái tài khoản của người dùng trong User View
     * @param updateAccountStatusEvent Trạng thái tài khoản mới của người dùng
     */
    void updateAccountStatus(UpdateAccountStatusEvent updateAccountStatusEvent);

    /**
     * Lấy danh sách User View với phân trang và lọc
     *
     * @param accountStatus Trạng thái tài khoản (optional)
     * @param role Vai trò người dùng (optional)
     * @param keyword Từ khóa tìm kiếm (optional)
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy Trường sắp xếp (mặc định là createdAt)
     * @param sortDir Hướng sắp xếp (mặc định là desc)
     * @return Trang chứa danh sách User View
     */
    PageResponse<UserViewDto> getUserViews(AccountStatus accountStatus, Role role, String keyword, int pageNo, int pageSize, String sortBy, String sortDir);


    void updateAvatarUser(UpdateAvatarUserEvent updateAvatarUserEvent);
}
