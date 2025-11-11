package com.ecommerce.library.kafka.event;

import com.ecommerce.library.enumeration.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountStatusEvent {
    private Long userId;
    private AccountStatus accountStatus;
}
