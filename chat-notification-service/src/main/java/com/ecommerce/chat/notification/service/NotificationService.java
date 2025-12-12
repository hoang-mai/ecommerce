package com.ecommerce.chat.notification.service;

import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.chat.notification.dto.NotificationDto;

public interface NotificationService {
    PageResponse<NotificationDto> getNotifications(int pageNo, int pageSize, String sortBy, String sortDir);

    void markAsRead(String notificationId);

    long getUnreadCount();

    void markAllAsRead();

}

