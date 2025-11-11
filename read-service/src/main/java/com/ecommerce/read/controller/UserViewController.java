package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.UserViewDto;
import com.ecommerce.read.service.UserViewService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Constant.USER_VIEW)
@RequiredArgsConstructor
public class UserViewController {
    private final UserViewService userViewService;
    private final MessageService messageService;

    /**
     * Lấy thông tin tài khoản và thông tin người dùng theo userId
     *
     * @param accountStatus Trạng thái tài khoản (optional)
     * @param role Vai trò người dùng (optional)
     * @param keyword Từ khóa tìm kiếm (optional)
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy Trường sắp xếp (mặc định là createdAt)
     * @param sortDir Hướng sắp xếp (mặc định là desc)
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<PageResponse<UserViewDto>>> getUserView(
            @RequestParam(required = false) AccountStatus accountStatus,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ){
        PageResponse<UserViewDto> userViews = userViewService.getUserViews(
                accountStatus, role, keyword, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(
                BaseResponse.<PageResponse<UserViewDto>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_INFO_USER_SUCCESS))
                        .data(userViews)
                        .build()
        );
    }

}
