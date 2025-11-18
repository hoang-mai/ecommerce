package com.ecommerce.notification.service;

import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.notification.dto.NotificationDto;

public interface NotificationService {
    PageResponse<NotificationDto> getNotifications(int pageNo, int pageSize, String sortBy, String sortDir);

    void markAsRead(Long notificationId);

    long getUnreadCount();

    void markAllAsRead();

}

