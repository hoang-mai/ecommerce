package com.ecommerce.notification.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.notification.dto.NotificationDto;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import com.ecommerce.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserHelper userHelper;

    @Override
    public PageResponse<NotificationDto> getNotifications(int pageNo, int pageSize, String sortBy, String sortDir) {
        Long userId = userHelper.getCurrentUserId();
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);

        return PageResponse.<NotificationDto>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(notifications.getTotalElements())
                .totalPages(notifications.getTotalPages())
                .hasNextPage(notifications.hasNext())
                .hasPreviousPage(notifications.hasPrevious())
                .data(notifications.getContent().stream()
                        .map(this::convertToResponse)
                        .toList())
                .build();
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException(MessageError.NOTIFICATION_NOT_FOUND));

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public long getUnreadCount() {
        Long userId = userHelper.getCurrentUserId();
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public void markAllAsRead() {
        Long userId = userHelper.getCurrentUserId();
        notificationRepository.markAllAsReadByUserId(userId);
    }

    private NotificationDto convertToResponse(Notification notification) {
        return NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .isRead(notification.getIsRead())
                .sentRealtime(notification.getSentRealtime())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}

