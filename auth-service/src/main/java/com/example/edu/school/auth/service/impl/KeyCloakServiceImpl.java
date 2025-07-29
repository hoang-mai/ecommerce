package com.example.edu.school.auth.service.impl;

import com.example.edu.school.auth.dto.account.ReqCreateAccountDTO;
import com.example.edu.school.auth.dto.auth.ReqLoginDTO;
import com.example.edu.school.auth.dto.keycloak.ResKeycloakLoginDTO;
import com.example.edu.school.library.enumeration.AccountStatus;
import com.example.edu.school.library.exception.DuplicateException;
import com.example.edu.school.auth.service.KeyCloakService;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeyCloakServiceImpl implements KeyCloakService {

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    private final Keycloak keycloak;

    private final PasswordEncoder passwordEncoder;


    @Override
    public ResKeycloakLoginDTO login(ReqLoginDTO reqLoginDTO) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", OAuth2Constants.PASSWORD);
        formData.add("client_id", "auth-service");
        formData.add("client_secret", clientSecret);
        formData.add("username", reqLoginDTO.getEmail());
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
    public String register(ReqCreateAccountDTO reqCreateAccountDTO, String email) {
        UserRepresentation user = mapperToUserRepresentation(reqCreateAccountDTO, email);
        try (Response response = keycloak.realm(realm).users().create(user)) {
            int status = response.getStatus();


            String errorBody = response.readEntity(String.class);
            System.out.println("Keycloak error response: " + errorBody);

            if (status == Response.Status.CONFLICT.getStatusCode()) {
                throw new DuplicateException("User already exists");
            }

            if (status != Response.Status.CREATED.getStatusCode()) {
                String reason = response.getStatusInfo() != null ? response.getStatusInfo().getReasonPhrase() : "Unknown";
                throw new RuntimeException("Failed to create user: " + reason + " (status: " + status + ")");
            }
            String accountId = response.getLocation().getPath().replaceAll(".*/users/", "");
            response.close();
            return accountId;

        }

    }

    /**
     * Chuyển đổi CreateAccountDTO thành UserRepresentation
     *
     * @param reqCreateAccountDTO DTO chứa thông tin tài khoản mới
     * @return UserRepresentation đại diện cho người dùng trong Keycloak
     */
    private UserRepresentation mapperToUserRepresentation(ReqCreateAccountDTO reqCreateAccountDTO, String email) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        Map<String, List<String>> attributes = Map.of("status", List.of(AccountStatus.ACTIVE.getStatus()));
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
