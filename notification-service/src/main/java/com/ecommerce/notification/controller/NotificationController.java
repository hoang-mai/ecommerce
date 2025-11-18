package com.ecommerce.notification.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.notification.dto.NotificationDto;
import com.ecommerce.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = Constant.NOTIFICATION)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final MessageService messageService;

    /**
     * Lấy danh sách thông báo với phân trang
     *
     * @param pageNo Số trang (mặc định là 0)
     * @param pageSize Kích thước trang (mặc định là 10)
     * @param sortBy Trường sắp xếp (mặc định là createdAt)
     * @param sortDir Hướng sắp xếp (mặc định là desc)
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<PageResponse<NotificationDto>>> getNotifications(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageResponse<NotificationDto> notifications = notificationService.getNotifications(
                pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(
                BaseResponse.<PageResponse<NotificationDto>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_NOTIFICATIONS_SUCCESS))
                        .data(notifications)
                        .build()
        );
    }

    /**
     * Đánh dấu thông báo là đã đọc
     *
     * @param notificationId ID của thông báo
     */
    @PatchMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<BaseResponse<Void>> markAsRead(
            @PathVariable Long notificationId) {

       notificationService.markAsRead(notificationId);

        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.UPDATE_NOTIFICATION_SUCCESS))
                        .build()
        );
    }

    /**
     * Lấy số lượng thông báo chưa đọc
     */
    @GetMapping("/unread-count")
    public ResponseEntity<BaseResponse<Long>> getUnreadCount() {
        long unreadCount = notificationService.getUnreadCount();

        return ResponseEntity.ok(
                BaseResponse.<Long>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_NOTIFICATIONS_SUCCESS))
                        .data(unreadCount)
                        .build()
        );
    }

    /**
     * Đánh dấu tất cả thông báo là đã đọc
     */
    @PutMapping("/mark-all-as-read")
    public ResponseEntity<BaseResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead();

        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.UPDATE_NOTIFICATION_SUCCESS))
                        .build()
        );
    }

}




