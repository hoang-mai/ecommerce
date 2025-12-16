package com.ecommerce.chat.notification.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscriptionRequest {

    @NotBlank(message = "Endpoint không được để trống")
    private String endpoint;

    @Valid
    private Keys keys;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Keys {
        @NotBlank(message = "p256dh không được để trống")
        private String p256dh;

        @NotBlank(message = "auth không được để trống")
        private String auth;
    }

}

