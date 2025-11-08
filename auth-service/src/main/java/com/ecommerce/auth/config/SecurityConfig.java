package com.ecommerce.auth.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public Keycloak keycloak(
            @Value("${keycloak.auth-server-url}") String authServerUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id-register}") String clientIdRegister,
            @Value("${keycloak.username}") String username,
            @Value("${keycloak.password}") String password) {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .clientId(clientIdRegister)
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
