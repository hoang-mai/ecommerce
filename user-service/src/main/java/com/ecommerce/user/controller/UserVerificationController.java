package com.ecommerce.user.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.user.dto.ReqRegisterOwnerDTO;
import com.ecommerce.user.dto.ReqRejectOwnerDTO;
import com.ecommerce.user.dto.ResUserVerificationDTO;
import com.ecommerce.library.enumeration.UserVerificationStatus;
import com.ecommerce.user.service.UserVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(Constant.USER_VERIFICATION)
@RequiredArgsConstructor
public class UserVerificationController {

    private final UserVerificationService userVerificationService;
    private final MessageService messageService;
    /**
     * Đăng ký yêu cầu làm chủ của hàng của người dùng hiện tại
     *
     * @param frontImage Ảnh mặt trước CCCD/CMND
     * @param backImage Ảnh mặt sau CCCD/CMND
     * @param avatar Ảnh chân dung
     * @param reqRegisterOwnerDTO Thông tin đăng ký chủ của hàng
     * @return Trả về thành công
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<BaseResponse<Void>> registerOwner(
            @RequestPart("frontImage") MultipartFile frontImage,
            @RequestPart("backImage") MultipartFile backImage,
            @RequestPart("avatar") MultipartFile avatar,
            @Valid @RequestPart("data") ReqRegisterOwnerDTO reqRegisterOwnerDTO) {
        userVerificationService.registerOwner(frontImage, backImage, avatar, reqRegisterOwnerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.REGISTER_OWNER_REQUEST_SUCCESS))
                .build());
    }


    /**
     * Từ chối yêu cầu làm chủ của hàng
     * @param userVerificationId ID của yêu cầu xác minh người dùng
     * @param reason Lý do từ chối
     * @return Trả về thành công
     */
    @PatchMapping("{userVerificationId}/reject")
    public ResponseEntity<BaseResponse<Void>> rejectOwner(
            @PathVariable Long userVerificationId,
            @Valid @RequestBody ReqRejectOwnerDTO reason) {
        userVerificationService.rejectOwnerRequest(userVerificationId, reason);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.REJECT_OWNER_REQUEST_SUCCESS))
                .build());
    }

    /**
     * Lấy thông tin xác minh của người dùng hiện tại
     *
     * @param status Trạng thái yêu cầu (PENDING, APPROVED, REJECTED)
     * @param keyword Từ khóa tìm kiếm (tìm theo CCCD, tên ngân hàng, tài khoản ngân hàng)
     * @param pageNo Số trang (mặc định: 0)
     * @param pageSize Kích thước trang (mặc định: 10)
     * @param sortBy Trường sắp xếp (mặc định: createdAt)
     * @param sortDir Hướng sắp xếp (asc/desc, mặc định: desc)
     * @return Thông tin xác minh của người dùng hiện tại với phân trang
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<PageResponse<ResUserVerificationDTO>>> getCurrentUserVerification(
            @RequestParam(required = false) UserVerificationStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {

        PageResponse<ResUserVerificationDTO> pageResponse = userVerificationService.getCurrentUserVerification(
                status, keyword, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(BaseResponse.<PageResponse<ResUserVerificationDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_USER_VERIFICATION_SUCCESS))
                .data(pageResponse)
                .build());
    }

    /**
     * Tìm kiếm và lọc các yêu cầu xác minh người dùng với phân trang và sắp xếp
     *
     * @param status Trạng thái yêu cầu (PENDING, APPROVED, REJECTED)
     * @param keyword Từ khóa tìm kiếm (tìm theo CCCD, tên người dùng, email, tên ngân hàng, tài khoản ngân hàng)
     * @param pageNo Số trang (mặc định: 0)
     * @param pageSize Kích thước trang (mặc định: 10)
     * @param sortBy Trường sắp xếp (mặc định: createdAt)
     * @param sortDir Hướng sắp xếp (asc/desc, mặc định: desc)
     * @return Danh sách yêu cầu xác minh với phân trang
     */
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<ResUserVerificationDTO>>> searchUserVerifications(
            @RequestParam(required = false) UserVerificationStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {

        PageResponse<ResUserVerificationDTO> pageResponse = userVerificationService.searchUserVerifications(
                status, keyword, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(BaseResponse.<PageResponse<ResUserVerificationDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.SEARCH_USER_VERIFICATION_SUCCESS))
                .data(pageResponse)
                .build());
    }
}
