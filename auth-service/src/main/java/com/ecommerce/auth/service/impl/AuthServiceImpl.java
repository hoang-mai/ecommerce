package com.ecommerce.auth.service.impl;

import com.ecommerce.auth.dto.auth.ReqUpdateAccountDTO;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.auth.dto.auth.ReqLoginDTO;
import com.ecommerce.auth.dto.auth.ResLoginDTO;
import com.ecommerce.auth.dto.keycloak.ResKeycloakLoginDTO;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.auth.service.AuthService;
import com.ecommerce.auth.service.KeyCloakService;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MessageService messageService;

    private final KeyCloakService keyCloakService;


    @Override
    public ResLoginDTO login(ReqLoginDTO reqLoginDTO) {
        try {
            ResKeycloakLoginDTO resKeycloakLoginDTO = keyCloakService.login(reqLoginDTO);

            if (resKeycloakLoginDTO == null) {
                throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
            }
            return ResLoginDTO.builder()
                    .accessToken(resKeycloakLoginDTO.getAccessToken())
                    .refreshToken(resKeycloakLoginDTO.getRefreshToken())
                    .expiresIn(resKeycloakLoginDTO.getExpiresIn())
                    .tokenType(resKeycloakLoginDTO.getTokenType())
                    .refreshExpiresIn(resKeycloakLoginDTO.getRefreshExpiresIn())
                    .sessionState(resKeycloakLoginDTO.getSessionState())
                    .scope(resKeycloakLoginDTO.getScope())
                    .build();
        } catch (HttpClientErrorException e) {
            String errorJson = e.getResponseBodyAsString();
            if (errorJson.contains("Invalid user credentials")) {
                throw new HttpRequestException(messageService.getMessage(MessageError.INVALID_USER_CREDENTIALS), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
            } else if (errorJson.contains("Account disabled")) {
                throw new HttpRequestException(messageService.getMessage(MessageError.ACCOUNT_DISABLED), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
            } else {
                throw new HttpRequestException(messageService.getMessage(MessageError.CANNOT_PARSE_ERROR_RESPONSE), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
            }

        } catch (Exception e) {
            throw new HttpRequestException(messageService.getMessage(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        }
    }

    @Override
    public void updateAccount(ReqUpdateAccountDTO reqUpdateStatusAccountDTO) {
        keyCloakService.updateAccount(reqUpdateStatusAccountDTO);
    }
}
