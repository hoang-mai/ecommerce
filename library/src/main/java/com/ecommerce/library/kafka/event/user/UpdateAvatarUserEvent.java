package com.ecommerce.library.kafka.event.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAvatarUserEvent {
    private Long userId;
    private String avatarUrl;
}
