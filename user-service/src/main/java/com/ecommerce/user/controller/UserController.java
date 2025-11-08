package com.ecommerce.user.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.exception.DuplicateException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.user.dto.*;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.user.service.UserService;
import com.ecommerce.library.utils.Constant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(Constant.USER)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MessageService messageService;

    /**
     * Tạo người dùng mới
     * 
     * @param reqCreateUserDTO Thông tin tạo người dùng mới
     * @return Trả về thành công
     */
    @PostMapping()
    public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody ReqCreateUserDTO reqCreateUserDTO) {
        userService.createUser(reqCreateUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.USER_CREATED_SUCCESS))
                .build());
    }

    /**
     * Lấy thông tin người dùng theo ID
     * 
     * @param userId ID của người dùng
     * @return Thông tin người dùng
     * @throws NotFoundException nếu người dùng không tồn tại
     */
    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<ResInfoUserDTO>> getUserById(@PathVariable Long userId) throws NotFoundException {
        ResInfoUserDTO userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(BaseResponse.<ResInfoUserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_INFO_USER_SUCCESS))
                .data(userResponse)
                .build());
    }

    /**
     * Lấy thông tin người dùng hiện tại
     *
     * @return Thông tin người dùng hiện tại
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<ResInfoUserDTO>> getCurrentUser() {
        ResInfoUserDTO userResponse = userService.getCurrentUser();
        return ResponseEntity.ok(BaseResponse.<ResInfoUserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_INFO_USER_SUCCESS))
                .data(userResponse)
                .build());
    }

    /**
     * Cập nhật thông tin người dùng hiện tại
     * @param reqUpdateUserDTO Thông tin cập nhật người dùng
     * @throws NotFoundException nếu người dùng không tồn tại
     * @throws DuplicateException nếu thông tin cập nhật trùng lặp (ví dụ: email đã tồn tại)
     * @return Trả về thành công
     */
    @PatchMapping()
    public ResponseEntity<BaseResponse<Void>> updateUser(
            @Valid @RequestBody ReqUpdateUserDTO reqUpdateUserDTO) throws NotFoundException, DuplicateException {
        userService.updateUser(reqUpdateUserDTO);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.UPDATE_USER_SUCCESS))
                .build());
    }

    /**
     * Upload ảnh đại diện cho người dùng hiện tại
     *
     * @param file Ảnh đại diện dưới dạng MultipartFile
     * @return Trả về thành công
     */
    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<BaseResponse<Void>> uploadAvatar(@RequestPart("file") MultipartFile file) {
        userService.uploadAvatar(file);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.UPLOAD_AVATAR_SUCCESS))
                .build());
    }


    /**
     * Tìm kiếm người dùng theo từ khóa
     *
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 5)
     * @param query Từ khóa tìm kiếm
     * @return Danh sách người dùng phù hợp với từ khóa
     */
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<ResInfoPreviewUserDTO>>>
    searchUsers(
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "query") String query
    ) {
        PageResponse<ResInfoPreviewUserDTO> listUserPageResponse =
                userService.searchUsers(pageNo, pageSize, query);
        return ResponseEntity.ok(BaseResponse.<PageResponse<ResInfoPreviewUserDTO>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.SEARCH_USER_SUCCESS))
                        .data(listUserPageResponse)
                        .build());
    }

}
