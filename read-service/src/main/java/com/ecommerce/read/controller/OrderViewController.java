package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.OrderViewStatisticDTO;
import com.ecommerce.read.entity.OrderView;
import com.ecommerce.read.service.OrderViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = Constant.ORDER_VIEW)
@RequiredArgsConstructor
public class OrderViewController {
    private final OrderViewService orderViewService;
    private final MessageService messageService;

    /**
     * Lấy thông tin đặt hàng của người dùng hiện tại
     *
     * @param orderStatus Trạng thái đơn hàng (optional)
     * @param keyword     Từ khóa tìm kiếm (optional)
     * @param productId   Id sản phẩm (optional)
     * @param shopId      Id cửa hàng (optional)
     * @param isOwner     Xác định người dùng hiện tại có phải là chủ sở hữu shop không (optional)
     * @param pageNo      Số trang (mặc định là 0)
     * @param pageSize    Kích thước trang (mặc định là 10)
     * @param sortBy      Trường sắp xếp (mặc định là createdAt)
     * @param sortDir     Hướng sắp xếp (mặc định là desc)
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<PageResponse<OrderView>>> getOrderView(
            @RequestParam(required = false) String shopId,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) Boolean isOwner,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        PageResponse<OrderView> orderViews = orderViewService.getOrderViews(
                shopId, isOwner, orderStatus, keyword, productId, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(
                BaseResponse.<PageResponse<OrderView>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_ORDER_SUCCESS))
                        .data(orderViews)
                        .build()
        );
    }

    /**
     * Thống kê đơn hàng theo trạng thái
     *
     * @param shopId  ID của cửa hàng (optional) - nếu cung cấp sẽ trả về thống kê cho cửa hàng đó (chỉ owner của shop)
     * @param isOwner Xác định người dùng hiện tại có phải là chủ sở hữu shop không (optional)
     * @param month   Tháng để lọc (1-12) - optional
     * @param year    Năm để lọc (ví dụ 2025) - optional, nếu không cung cấp sẽ dùng năm hiện tại
     * @return Thống kê đơn hàng theo trạng thái
     */
    @GetMapping("/statistic")
    public ResponseEntity<BaseResponse<Map<OrderStatus, Long>>> getOrderStatistics(
            @RequestParam(required = false) String shopId,
            @RequestParam(required = false) Boolean isOwner,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        Map<OrderStatus, Long> stats = orderViewService.getOrderStatistics(shopId, isOwner, month, year);
        return ResponseEntity.ok(
                BaseResponse.<Map<OrderStatus, Long>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_ORDER_SUCCESS))
                        .data(stats)
                        .build()
        );
    }

    /**
     * Thống kê đơn hàng tạo mới theo từ ngày nào đến ngày nào
     * @param shopId  ID của cửa hàng (optional) - nếu cung cấp sẽ trả về thống kê cho cửa hàng đó (chỉ owner của shop)
     * @param isOwner Xác định người dùng hiện tại có phải là chủ sở hữu shop không (optional)
     * @param fromDate Ngày bắt đầu (optional)
     * @param toDate Ngày kết thúc (optional)
     * @return Thống kê số lượng đơn hàng tạo mới trong ngày theo từ ngày nào đến ngày nào
     */
    @GetMapping("/statistic/date-range")
    public ResponseEntity<BaseResponse<List<OrderViewStatisticDTO>>> getOrderStatisticsByDateRange(
            @RequestParam(required = false) String shopId,
            @RequestParam(required = false) Boolean isOwner,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        List<OrderViewStatisticDTO> stats = orderViewService.getOrderStatisticsByDateRange(shopId, isOwner, fromDate, toDate);
        return ResponseEntity.ok(
                BaseResponse.<List<OrderViewStatisticDTO>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_ORDER_SUCCESS))
                        .data(stats)
                        .build()
        );
    }
}

