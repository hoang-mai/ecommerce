package com.ecommerce.auth.service.impl;

import com.ecommerce.auth.ReqCreateAccountDTO;
import com.ecommerce.auth.dto.auth.ReqLoginDTO;
import com.ecommerce.auth.dto.auth.ReqUpdateAccountDTO;
import com.ecommerce.auth.dto.keycloak.ResKeycloakLoginDTO;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.exception.DuplicateException;
import com.ecommerce.auth.service.KeyCloakService;

import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeyCloakServiceImpl implements KeyCloakService {

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.client-id-login}")
    private String clientIdLogin;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    private final Keycloak keycloak;

    private final UserHelper userHelper;

    @Override
    public ResKeycloakLoginDTO login(ReqLoginDTO reqLoginDTO) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", OAuth2Constants.PASSWORD);
        formData.add("client_id", clientIdLogin);
        formData.add("client_secret", clientSecret);
        formData.add("username", reqLoginDTO.getUsername());
        formData.add("password", reqLoginDTO.getPassword());
        return RestClient.builder()
                .baseUrl(authServerUrl)
                .defaultHeader("Content-Type", "application/x-www-form-urlencoded")
                .defaultHeader("Accept", "application/json")
                .build()
                .post()
                .uri("/realms/" + realm + "/protocol/openid-connect/token")
                .body(formData)
                .retrieve()
                .body(ResKeycloakLoginDTO.class);
    }


    @Override
    public String register(ReqCreateAccountDTO reqCreateAccountDTO) {
        UserRepresentation user = mapperToUserRepresentation(reqCreateAccountDTO);
        Response response = keycloak.realm(realm).users().create(user);
        int status = response.getStatus();

        if (status == Response.Status.CONFLICT.getStatusCode()) {
            throw new DuplicateException(MessageError.ACCOUNT_DUPLICATE);
        }
        if (status != Response.Status.CREATED.getStatusCode()) {
            throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        }
        return response.getLocation().getPath().replaceAll(".*/users/", "");


    }

    @Override
    public void delete(String accountId) {
        try (Response response = keycloak.realm(realm).users().delete(accountId)) {
            int status = response.getStatus();
            if (status != Response.Status.NO_CONTENT.getStatusCode()) {
                throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
            }
        } catch (Exception e) {
            throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        }
    }

    @Override
    public void updateAccount(ReqUpdateAccountDTO reqUpdateAccountDTO) {
        if (FnCommon.isNotNull(reqUpdateAccountDTO.getAccountStatus())) {
            updateAccountStatus(reqUpdateAccountDTO.getAccountStatus());
        } else if (FnCommon.isNotNullOrEmpty(reqUpdateAccountDTO.getCurrentPassword()) && FnCommon.isNotNullOrEmpty(reqUpdateAccountDTO.getNewPassword())) {
            updatePassword(reqUpdateAccountDTO.getNewPassword());
        }
    }

    @Override
    public void logout() {
        String accountId = userHelper.getAccountId();
        keycloak.realm(realm).users().get(accountId).logout();
    }

    @Override
    public void adminUpdateAccountStatus(ReqUpdateAccountDTO reqUpdateAccountDTO, String accountId) {
        UserRepresentation user = keycloak.realm(realm).users().get(accountId).toRepresentation();
        if (user == null) {
            throw new HttpRequestException(MessageError.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
        }
        if (FnCommon.isNotNull(reqUpdateAccountDTO.getAccountStatus())) {
            user.setEnabled(AccountStatus.ACTIVE.equals(reqUpdateAccountDTO.getAccountStatus()));
        }
        try {
            keycloak.realm(realm).users().get(accountId).update(user);
        } catch (Exception e) {
            throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        }
    }

    /**
     * Cập nhật mật khẩu tài khoản trong Keycloak
     *
     * @param newPassword mật khẩu mới
     */
    private void updatePassword(String newPassword) {
        String accountId = userHelper.getAccountId();

        UserRepresentation user = keycloak.realm(realm).users().get(accountId).toRepresentation();
        if (user == null) {
            throw new HttpRequestException(MessageError.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
        }

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        try {
            keycloak.realm(realm).users().get(accountId).update(user);
        } catch (Exception e) {
            throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        }
    }

    /**
     * Cập nhật trạng thái tài khoản trong Keycloak
     *
     * @param accountStatus trạng thái tài khoản mới
     */
    private void updateAccountStatus(AccountStatus accountStatus) {
        String accountId = userHelper.getAccountId();
        UserRepresentation user = keycloak.realm(realm).users().get(accountId).toRepresentation();
        if (user == null) {
            throw new HttpRequestException(MessageError.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
        }
        user.setEnabled(AccountStatus.ACTIVE.equals(accountStatus));
        try {
            keycloak.realm(realm).users().get(accountId).update(user);
        } catch (Exception e) {
            throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        }
    }

    /**
     * Chuyển đổi CreateAccountDTO thành UserRepresentation
     *
     * @param reqCreateAccountDTO DTO chứa thông tin tài khoản mới
     * @return UserRepresentation đại diện cho người dùng trong Keycloak
     */
    private UserRepresentation mapperToUserRepresentation(ReqCreateAccountDTO reqCreateAccountDTO) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(reqCreateAccountDTO.getUsername());
        Map<String, List<String>> attributes = Map.of(
                "userId", List.of(String.valueOf(reqCreateAccountDTO.getUserId())),
                "role", List.of(FnCommon.convertRoleProtoToRole(reqCreateAccountDTO.getRole()).getRole())
        );
        user.setAttributes(attributes);
        user.setEnabled(true);
        user.setEmailVerified(true);
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(reqCreateAccountDTO.getPassword());
        credential.setTemporary(false);
        List<CredentialRepresentation> credentialRepresentations = List.of(credential);
        user.setCredentials(credentialRepresentations);
        return user;
    }


}
