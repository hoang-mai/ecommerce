package com.example.app.chat.library.component;

import com.example.app.chat.library.utils.MessageError;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;
@Component
public class UserHelper {
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt){
            String userId = jwt.getClaimAsString("user-id");
            if (userId != null) {
                return Long.valueOf(userId);
            } else {
                throw new IllegalStateException(MessageError.USER_ID_NOT_FOUND);
            }
        } else {
            throw new IllegalStateException(MessageError.USER_ID_NOT_FOUND);
        }
    }

    public String getAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt){
            String accountId = jwt.getClaimAsString("account-id");
            if (accountId != null) {
                return accountId;
            } else {
                throw new IllegalStateException(MessageError.ACCOUNT_ID_NOT_FOUND);
            }
        } else {
            throw new IllegalStateException(MessageError.ACCOUNT_ID_NOT_FOUND);
        }
    }

    public String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt){
            String token = jwt.getTokenValue();
            if (token != null) {
                return token;
            } else {
                throw new IllegalStateException(MessageError.TOKEN_NOT_FOUND);
            }
        } else {
            throw new IllegalStateException(MessageError.TOKEN_NOT_FOUND);
        }
    }
}
