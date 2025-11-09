package com.ecommerce.user.saga.service;

import com.ecommerce.user.enumeration.UserVerificationStatus;

public interface UserVerificationServiceSaga {
    void updateUserVerificationStatus(Long userVerificationId, UserVerificationStatus userVerificationStatus);
}
