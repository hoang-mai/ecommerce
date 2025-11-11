package com.ecommerce.user.service;

import com.ecommerce.library.enumeration.Role;
import com.ecommerce.user.ReqCreateUserDTO;
import com.ecommerce.user.ReqRollbackUpdateUserRoleAndVerificationStatusDTO;
import com.ecommerce.user.ReqUpdateUserRoleAndVerificationStatusDTO;
import com.ecommerce.user.ResUpdateUserRoleAndVerificationStatusDTO;
import com.ecommerce.user.dto.*;
import com.ecommerce.library.utils.PageResponse;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {


    /**
     * Tạo người dùng mới với thông tin từ CreateUserData.
     *
     * @param reqCreateUserDTO DTO chứa thông tin người dùng mới
     * @return userId của người dùng đã được tạo
     */
    Long createUser(ReqCreateUserDTO reqCreateUserDTO);

    /**
     * Lấy thông tin người dùng theo ID.
     *
     * @param id ID của người dùng
     * @return Thông tin người dùng
     */
    ResInfoUserDTO getUserById(Long id);


    /**
     * Cập nhật thông tin người dùng theo ID.
     *
     * @param reqUpdateUserDTO DTO chứa thông tin cập nhật người dùng
     */
    void updateUser(ReqUpdateUserDTO reqUpdateUserDTO);

    /**
     * Xoá người dùng theo ID.
     *
     * @param userId ID của người dùng cần xoá
     */
    void deleteUser(Long userId);


    /**
     * Tìm kiếm người dùng theo tên  hoặc email.
     *
     * @param pageNo   Số trang
     * @param pageSize Kích thước trang
     * @param query    Chuỗi tìm kiếm (tên  hoặc email)
     * @return Danh sách người dùng phù hợp với trang và kích thước trang
     */
    PageResponse<ResInfoPreviewUserDTO> searchUsers(int pageNo, int pageSize, String query);

    /**
     * Lấy thông tin người dùng hiện tại theo ID.
     *
     * @return Thông tin người dùng hiện tại
     */
    ResInfoUserDTO getCurrentUser();

    /**
     * Tải lên avatar cho người dùng hiện tại.
     *
     * @param file Tệp avatar cần tải lên
     */
    void uploadAvatar(MultipartFile file, Boolean isDelete);

    void rollbackUpdateUserRoleAndVerificationStatus(ReqRollbackUpdateUserRoleAndVerificationStatusDTO reqRollbackUpdateUserRoleAndVerificationStatusDTO);

    ResUpdateUserRoleAndVerificationStatusDTO updateUserRoleAndVerificationStatus(ReqUpdateUserRoleAndVerificationStatusDTO reqUpdateUserRoleAndVerificationStatusDTO);
}
