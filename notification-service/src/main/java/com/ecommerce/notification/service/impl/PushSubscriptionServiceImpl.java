package com.ecommerce.notification.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.notification.dto.PushSubscriptionDto;
import com.ecommerce.notification.dto.PushSubscriptionRequest;
import com.ecommerce.notification.entity.PushSubscription;
import com.ecommerce.notification.repository.PushSubscriptionRepository;
import com.ecommerce.notification.service.PushSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserHelper userHelper;

    @Override
    public PushSubscriptionDto subscribe(PushSubscriptionRequest request) {
        Long userId = userHelper.getCurrentUserId();

        // Kiểm tra xem subscription đã tồn tại chưa
        if (pushSubscriptionRepository.existsByEndpointAndUserId(request.getEndpoint(), userId)) {
            // Nếu tồn tại, activate lại
            PushSubscription existing = pushSubscriptionRepository.findByEndpoint(request.getEndpoint())
                    .orElseThrow(() -> new NotFoundException("Subscription không tồn tại"));
            existing.setActive(true);
            PushSubscription saved = pushSubscriptionRepository.save(existing);
            return convertToDto(saved);
        }

        // Tạo mới subscription
        PushSubscription pushSubscription = PushSubscription.builder()
                .userId(userId)
                .endpoint(request.getEndpoint())
                .p256dh(request.getP256dh())
                .auth(request.getAuth())
                .active(true)
                .build();

        PushSubscription saved = pushSubscriptionRepository.save(pushSubscription);
        return convertToDto(saved);
    }

    @Override
    public void unsubscribe(String endpoint) {
        PushSubscription pushSubscription = pushSubscriptionRepository.findByEndpoint(endpoint)
                .orElseThrow(() -> new NotFoundException("Subscription không tồn tại"));
        pushSubscriptionRepository.delete(pushSubscription);
    }

    @Override
    public List<PushSubscriptionDto> getUserSubscriptions() {
        Long userId = userHelper.getCurrentUserId();
        List<PushSubscription> subscriptions = pushSubscriptionRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<PushSubscriptionDto> getActiveSubscriptions() {
        Long userId = userHelper.getCurrentUserId();
        List<PushSubscription> subscriptions = pushSubscriptionRepository.findActiveByUserId(userId);
        return subscriptions.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public void deactivateSubscription(Long subscriptionId) {
        PushSubscription pushSubscription = pushSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription không tồn tại"));
        pushSubscription.setActive(false);
        pushSubscriptionRepository.save(pushSubscription);
    }

    @Override
    public void activateSubscription(Long subscriptionId) {
        PushSubscription pushSubscription = pushSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription không tồn tại"));
        pushSubscription.setActive(true);
        pushSubscriptionRepository.save(pushSubscription);
    }

    private PushSubscriptionDto convertToDto(PushSubscription pushSubscription) {
        return PushSubscriptionDto.builder()
                .pushSubscriptionId(pushSubscription.getPushSubscriptionId())
                .userId(pushSubscription.getUserId())
                .endpoint(pushSubscription.getEndpoint())
                .p256dh(pushSubscription.getP256dh())
                .auth(pushSubscription.getAuth())
                .active(pushSubscription.isActive())
                .createdAt(pushSubscription.getCreatedAt())
                .updatedAt(pushSubscription.getUpdatedAt())
                .build();
    }
}

