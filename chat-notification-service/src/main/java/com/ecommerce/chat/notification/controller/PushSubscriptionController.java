package com.ecommerce.chat.notification.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.chat.notification.dto.PushSubscriptionRequest;
import com.ecommerce.chat.notification.service.PushSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = Constant.PUSH_SUBSCRIPTION)
@RequiredArgsConstructor
public class PushSubscriptionController {

    private final PushSubscriptionService pushSubscriptionService;
    private final MessageService messageService;

    /**
     * Subscribe nhận push notification
     */
    @PostMapping("/subscribe")
    public ResponseEntity<BaseResponse<Void>> subscribe(
            @Valid @RequestBody PushSubscriptionRequest request) {

        pushSubscriptionService.subscribe(request);

        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .statusCode(200)
                        .message(messageService.getMessage(MessageSuccess.PUSH_SUBSCRIPTION_SUCCESS))
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

}

