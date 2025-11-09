package com.ecommerce.user.saga.service.impl;

import com.ecommerce.user.enumeration.UserVerificationStatus;
import com.ecommerce.user.saga.service.UserVerificationServiceSaga;
import com.ecommerce.user.service.UserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserVerificationServiceSagaImpl implements UserVerificationServiceSaga {
    private final UserVerificationService userVerificationService;

    @Override
    public void updateUserVerificationStatus(Long userVerificationId, UserVerificationStatus userVerificationStatus) {
        userVerificationService.updateUserVerificationStatus(userVerificationId, userVerificationStatus);
    }
}
