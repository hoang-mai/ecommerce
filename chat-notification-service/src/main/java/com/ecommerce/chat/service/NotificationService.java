package com.ecommerce.chat.service;

import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.chat.dto.NotificationDto;

public interface NotificationService {
    PageResponse<NotificationDto> getNotifications(int pageNo, int pageSize, String sortBy, String sortDir);

    void markAsRead(String notificationId);

    long getUnreadCount();

    void markAllAsRead();

}

