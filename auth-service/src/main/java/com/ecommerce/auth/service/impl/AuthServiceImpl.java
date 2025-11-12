package com.ecommerce.auth.service.impl;

import com.ecommerce.auth.ReqCreateAccountDTO;
import com.ecommerce.auth.dto.auth.*;
import com.ecommerce.auth.dto.keycloak.ResKeyCloakRefreshTokenDTO;
import com.ecommerce.auth.entity.Account;
import com.ecommerce.auth.producer.UserEventProducer;
import com.ecommerce.auth.repository.AccountRepository;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.auth.dto.keycloak.ResKeycloakLoginDTO;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.auth.service.AuthService;
import com.ecommerce.auth.service.KeyCloakService;
import com.ecommerce.library.kafka.event.user.UpdateAccountStatusEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MessageService messageService;
    private final AccountRepository accountRepository;
    private final KeyCloakService keyCloakService;
    private final PasswordEncoder passwordEncoder;
    private final UserHelper userHelper;
    private final UserEventProducer userEventProducer;


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
                throw new HttpRequestException(messageService.getMessage(MessageError.INVALID_USER_CREDENTIALS), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
            } else if (errorJson.contains("Account disabled")) {
                throw new HttpRequestException(messageService.getMessage(MessageError.ACCOUNT_DISABLED), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
            } else {
                throw new HttpRequestException(messageService.getMessage(MessageError.CANNOT_PARSE_ERROR_RESPONSE), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
            }

        } catch (Exception e) {
            throw new HttpRequestException(messageService.getMessage(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        }
    }

    @Transactional
    @Override
    public void updateAccount(ReqUpdateAccountDTO reqUpdateStatusAccountDTO) {
        Account account = accountRepository.findById(userHelper.getCurrentUserId())
                .orElseThrow(() -> new HttpRequestException(messageService.getMessage(MessageError.ACCOUNT_NOT_FOUND), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
        if (FnCommon.isNotNull(reqUpdateStatusAccountDTO.getAccountStatus())) {
            account.setAccountStatus(reqUpdateStatusAccountDTO.getAccountStatus());
            userEventProducer.send(UpdateAccountStatusEvent.builder()
                    .userId(account.getUserId())
                    .accountStatus(reqUpdateStatusAccountDTO.getAccountStatus())
                    .build());
        }
        if (FnCommon.isNotNullOrEmpty(reqUpdateStatusAccountDTO.getCurrentPassword()) && FnCommon.isNotNullOrEmpty(reqUpdateStatusAccountDTO.getNewPassword())) {
            if (!passwordEncoder.matches(reqUpdateStatusAccountDTO.getCurrentPassword(), account.getPassword())) {
                throw new HttpRequestException(messageService.getMessage(MessageError.CURRENT_PASSWORD_INCORRECT), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
            }
            account.setPassword(passwordEncoder.encode(reqUpdateStatusAccountDTO.getNewPassword()));
        }
        accountRepository.save(account);
        keyCloakService.updateAccount(reqUpdateStatusAccountDTO);
    }

    @Override
    public void logout() {
        keyCloakService.logout();
    }

    @Transactional
    @Override
    public void adminUpdateAccountStatus(ReqUpdateAccountDTO reqUpdateAccountDTO, Long userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new HttpRequestException(messageService.getMessage(MessageError.ACCOUNT_NOT_FOUND), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
        if (FnCommon.isNotNull(reqUpdateAccountDTO.getAccountStatus())) {
            account.setAccountStatus(reqUpdateAccountDTO.getAccountStatus());
        }
        accountRepository.save(account);
        keyCloakService.adminUpdateAccountStatus(reqUpdateAccountDTO, account.getAccountId());
        userEventProducer.send(UpdateAccountStatusEvent.builder()
                .userId(userId)
                .accountStatus(reqUpdateAccountDTO.getAccountStatus())
                .build());
    }

    @Override
    public void createAccount(ReqCreateAccountDTO reqCreateAccountDTO, String accountId) {
        Account account = Account.builder()
                .userId(reqCreateAccountDTO.getUserId())
                .accountId(accountId)
                .username(reqCreateAccountDTO.getUsername())
                .accountStatus(FnCommon.convertAccountStatusProtoToAccountStatus(reqCreateAccountDTO.getAccountStatus()))
                .password(passwordEncoder.encode(reqCreateAccountDTO.getPassword()))
                .build();
        accountRepository.save(account);
    }

    @Override
    public void deleteAccount(String accountId) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new HttpRequestException(messageService.getMessage(MessageError.ACCOUNT_NOT_FOUND), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
        accountRepository.delete(account);
    }

    @Override
    public void updateRole(long userId, Role role) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new HttpRequestException(messageService.getMessage(MessageError.ACCOUNT_NOT_FOUND), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()));
        keyCloakService.updateRole(role, account.getAccountId());
    }

    @Override
    public ResRefreshTokenDTO refreshToken(ReqRefreshTokenDTO reqRefreshTokenDTO) {
        try {
            ResKeyCloakRefreshTokenDTO resKeyCloakRefreshTokenDTO = keyCloakService.refreshToken(reqRefreshTokenDTO);
            if (resKeyCloakRefreshTokenDTO == null) {
                throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
            }
            return ResRefreshTokenDTO.builder()
                    .accessToken(resKeyCloakRefreshTokenDTO.getAccessToken())
                    .expiresIn(resKeyCloakRefreshTokenDTO.getExpiresIn())
                    .refreshExpiresIn(resKeyCloakRefreshTokenDTO.getRefreshExpiresIn())
                    .refreshToken(resKeyCloakRefreshTokenDTO.getRefreshToken())
                    .tokenType(resKeyCloakRefreshTokenDTO.getTokenType())
                    .refreshExpiresIn(resKeyCloakRefreshTokenDTO.getRefreshExpiresIn())
                    .notBeforePolicy(resKeyCloakRefreshTokenDTO.getNotBeforePolicy())
                    .scope(resKeyCloakRefreshTokenDTO.getScope())
                    .build();
        } catch (Exception e) {
            throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        }
    }
}
