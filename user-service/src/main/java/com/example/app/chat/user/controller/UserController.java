package com.example.app.chat.user.controller;

import com.example.app.chat.library.component.MessageService;
import com.example.app.chat.library.exception.DuplicateException;
import com.example.app.chat.library.exception.NotFoundException;
import com.example.app.chat.library.utils.PageResponse;
import com.example.app.chat.user.dto.ReqUpdateUserDTO;
import com.example.app.chat.user.dto.ReqCreateUserDTO;
import com.example.app.chat.library.utils.BaseResponse;
import com.example.app.chat.library.utils.MessageSuccess;
import com.example.app.chat.user.dto.ResInfoPreviewUserDTO;
import com.example.app.chat.user.dto.ResInfoUserDTO;
import com.example.app.chat.user.service.UserService;
import com.example.app.chat.library.utils.Constant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
    @PostMapping("/create")
    public ResponseEntity<BaseResponse<String>> register(@Valid @RequestBody ReqCreateUserDTO reqCreateUserDTO) {
        log.info("Registering user: {}", reqCreateUserDTO);
        userService.createUser(reqCreateUserDTO);
        log.info("User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<String>builder()
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
        log.info("Fetching user by id: {}", userId);
        ResInfoUserDTO userResponse = userService.getUserById(userId);
        log.info("User fetched successfully: {}", userResponse);
        return ResponseEntity.ok(BaseResponse.<ResInfoUserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_INFO_USER_SUCCESS))
                .data(userResponse)
                .build());
    }

    /**
     * Cập nhật thông tin người dùng theo ID
     * @param userId ID của người dùng hiện tại
     * @param reqUpdateUserDTO Thông tin cập nhật người dùng
     * @throws NotFoundException nếu người dùng không tồn tại
     * @throws DuplicateException nếu thông tin cập nhật trùng lặp (ví dụ: email đã tồn tại)
     * @return Trả về thành công
     */
    @PatchMapping()
    public ResponseEntity<BaseResponse<Void>> updateUserById(@RequestHeader("user-id") Long userId,
            @RequestBody ReqUpdateUserDTO reqUpdateUserDTO) throws NotFoundException, DuplicateException {
        log.info("Updating user with id: {}", userId);
        userService.updateUserById(userId, reqUpdateUserDTO);
        log.info("User updated successfully: {}", userId);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.UPDATE_USER_SUCCESS))
                .build());
    }

    /**
     * Lấy thông tin người dùng hiện tại
     *
     * @param userId ID của người dùng hiện tại
     * @return Thông tin người dùng hiện tại
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<ResInfoUserDTO>> getCurrentUser(@RequestHeader("user-id") Long userId) {
        ResInfoUserDTO userResponse = userService.getCurrentUserById(userId);
        log.info("Current user fetched successfully: {}", userResponse);
        return ResponseEntity.ok(BaseResponse.<ResInfoUserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_INFO_USER_SUCCESS))
                .data(userResponse)
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
    public ResponseEntity<BaseResponse<PageResponse<List<ResInfoPreviewUserDTO>>>>
    searchUsers(
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "query") String query
    ) {
        log.info("Searching users with query: {}", query);
        PageResponse<List<ResInfoPreviewUserDTO>> listUserPageResponse =
                userService.searchUsers(pageNo, pageSize, query);
        log.info("User search results: {}", listUserPageResponse);
        return
                ResponseEntity.ok(BaseResponse.<PageResponse<List<ResInfoPreviewUserDTO>>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.SEARCH_USER_SUCCESS))
                        .data(listUserPageResponse)
                        .build());
    }

}
