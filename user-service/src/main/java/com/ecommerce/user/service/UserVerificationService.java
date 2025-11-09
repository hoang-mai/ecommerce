package com.ecommerce.user.service;

import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.user.dto.ReqRegisterOwnerDTO;
import com.ecommerce.user.dto.ReqRejectOwnerDTO;
import com.ecommerce.user.dto.ResUserVerificationDTO;
import com.ecommerce.user.enumeration.UserVerificationStatus;
import org.springframework.web.multipart.MultipartFile;

public interface UserVerificationService {

    /**
     * Đăng ký yêu cầu làm người bán.
     *
     * @param frontImage Ảnh mặt trước CCCD/CMND
     * @param backImage Ảnh mặt sau CCCD/CMND
     * @param avatar Ảnh chân dung
     * @param reqRegisterOwnerDTO DTO chứa thông tin đăng ký người bán
     */
    void registerOwner(MultipartFile frontImage, MultipartFile backImage, MultipartFile avatar, ReqRegisterOwnerDTO reqRegisterOwnerDTO);

    void approveOwnerRequest(Long userVerificationId);

    void rejectOwnerRequest(Long userVerificationId, ReqRejectOwnerDTO reason);

    /**
     * Tìm kiếm và phân trang các yêu cầu xác minh người dùng.
     *
     * @param status Trạng thái yêu cầu (nullable)
     * @param keyword Từ khóa tìm kiếm (nullable)
     * @param pageNo Số trang
     * @param pageSize Kích thước trang
     * @param sortBy Trường sắp xếp
     * @param sortDir Hướng sắp xếp (asc/desc)
     * @return PageResponse chứa danh sách các yêu cầu xác minh
     */
    PageResponse<ResUserVerificationDTO> searchUserVerifications(
            UserVerificationStatus status,
            String keyword,
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir
    );

    /**
     * Lấy thông tin xác minh của người dùng hiện tại
     *
     * @param status Trạng thái yêu cầu (nullable)
     * @param keyword Từ khóa tìm kiếm (nullable)
     * @param pageNo Số trang
     * @param pageSize Kích thước trang
     * @param sortBy Trường sắp xếp
     * @param sortDir Hướng sắp xếp (asc/desc)
     * @return PageResponse chứa thông tin xác minh của người dùng
     */
    PageResponse<ResUserVerificationDTO> getCurrentUserVerification(
            UserVerificationStatus status,
            String keyword,
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir);

    void updateUserVerificationStatus(Long userVerificationId, UserVerificationStatus userVerificationStatus);
}
