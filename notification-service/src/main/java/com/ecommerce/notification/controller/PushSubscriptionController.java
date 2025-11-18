package com.ecommerce.notification.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.notification.dto.PushSubscriptionDto;
import com.ecommerce.notification.dto.PushSubscriptionRequest;
import com.ecommerce.notification.service.PushSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = Constant.NOTIFICATION + "/subscriptions")
@RequiredArgsConstructor
public class PushSubscriptionController {

    private final PushSubscriptionService pushSubscriptionService;
    private final MessageService messageService;

    /**
     * Subscribe nhận push notification
     */
    @PostMapping("/subscribe")
    public ResponseEntity<BaseResponse<PushSubscriptionDto>> subscribe(
            @Valid @RequestBody PushSubscriptionRequest request) {

        PushSubscriptionDto subscription = pushSubscriptionService.subscribe(request);

        return ResponseEntity.ok(
                BaseResponse.<PushSubscriptionDto>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.PUSH_SUBSCRIPTION_SUCCESS))
                        .data(subscription)
                        .build()
        );
    }

    /**
     * Unsubscribe từ push notification
     */
    @DeleteMapping("/unsubscribe")
    public ResponseEntity<BaseResponse<Void>> unsubscribe(
            @RequestParam String endpoint) {

        pushSubscriptionService.unsubscribe(endpoint);

        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.PUSH_UNSUBSCRIPTION_SUCCESS))
                        .build()
        );
    }

    /**
     * Lấy tất cả subscription của user
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<List<PushSubscriptionDto>>> getUserSubscriptions() {

        List<PushSubscriptionDto> subscriptions = pushSubscriptionService.getUserSubscriptions();

        return ResponseEntity.ok(
                BaseResponse.<List<PushSubscriptionDto>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_NOTIFICATIONS_SUCCESS))
                        .data(subscriptions)
                        .build()
        );
    }

    /**
     * Lấy tất cả subscription đang active của user
     */
    @GetMapping("/active")
    public ResponseEntity<BaseResponse<List<PushSubscriptionDto>>> getActiveSubscriptions() {

        List<PushSubscriptionDto> subscriptions = pushSubscriptionService.getActiveSubscriptions();

        return ResponseEntity.ok(
                BaseResponse.<List<PushSubscriptionDto>>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.GET_NOTIFICATIONS_SUCCESS))
                        .data(subscriptions)
                        .build()
        );
    }

    /**
     * Deactivate một subscription
     */
    @PatchMapping("/{subscriptionId}/deactivate")
    public ResponseEntity<BaseResponse<Void>> deactivateSubscription(
            @PathVariable Long subscriptionId) {

        pushSubscriptionService.deactivateSubscription(subscriptionId);

        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.UPDATE_NOTIFICATION_SUCCESS))
                        .build()
        );
    }

    /**
     * Activate một subscription
     */
    @PatchMapping("/{subscriptionId}/activate")
    public ResponseEntity<BaseResponse<Void>> activateSubscription(
            @PathVariable Long subscriptionId) {

        pushSubscriptionService.activateSubscription(subscriptionId);

        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.UPDATE_NOTIFICATION_SUCCESS))
                        .build()
        );
    }
}

