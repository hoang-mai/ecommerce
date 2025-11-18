package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.OrderView;
import com.ecommerce.read.service.OrderViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Constant.ORDER_VIEW)
@RequiredArgsConstructor
public class OrderViewController {
    private final OrderViewService orderViewService;
    private final MessageService messageService;

    /**
     * Lấy thông tin đặt hàng theo orderId
     *
     * @param orderStatus Trạng thái đơn hàng (optional)
     * @param keyword Từ khóa tìm kiếm (optional)
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy Trường sắp xếp (mặc định là createdAt)
     * @param sortDir Hướng sắp xếp (mặc định là desc)
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<PageResponse<OrderView>>> getOrderView(
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ){
        PageResponse<OrderView> orderViews = orderViewService.getOrderViews(
                orderStatus, keyword, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(
                BaseResponse.<PageResponse<OrderView>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_ORDER_SUCCESS))
                        .data(orderViews)
                        .build()
        );
    }

}
