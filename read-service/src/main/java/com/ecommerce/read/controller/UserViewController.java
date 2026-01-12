package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.AddressDTO;
import com.ecommerce.read.dto.NewUserViewStatisticDTO;
import com.ecommerce.read.entity.UserView;
import com.ecommerce.read.service.UserViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

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
    public ResponseEntity<BaseResponse<PageResponse<UserView>>> getUserView(
            @RequestParam(required = false) AccountStatus accountStatus,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ){
        PageResponse<UserView> userViews = userViewService.getUserViews(
                accountStatus, role, keyword, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(
                BaseResponse.<PageResponse<UserView>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_INFO_USER_SUCCESS))
                        .data(userViews)
                        .build()
        );
    }

    /**
     * Tìm kiếm địa chỉ
     *
     * @param keyword Từ khóa tìm kiếm
     */
    @GetMapping("/search-address")
    public ResponseEntity<BaseResponse<List<String>>> searchAddress(
            @RequestParam String keyword
    ){
        List<String> addresses = userViewService.searchAddress(keyword);
        return ResponseEntity.ok(
                BaseResponse.<List<String>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.SEARCH_ADDRESS_SUCCESS))
                        .data(addresses)
                        .build()
        );
    }

    @GetMapping("/statistic/date-range")
    public ResponseEntity<BaseResponse<List<NewUserViewStatisticDTO>>> getUserAddressInDateRange(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate){
        List<NewUserViewStatisticDTO> stats = userViewService.getUserStatisticsByDateRange(fromDate, toDate);
        return ResponseEntity.ok(
                BaseResponse.<List<NewUserViewStatisticDTO>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_USER_STATISTIC_SUCCESS))
                        .data(stats)
                        .build()
        );
    }
}
