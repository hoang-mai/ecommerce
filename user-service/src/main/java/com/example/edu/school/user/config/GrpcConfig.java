package com.example.edu.school.user.config;

import com.example.edu.school.auth.AccountServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

@Configuration
public class GrpcConfig {

    @Bean
    AccountServiceGrpc.AccountServiceBlockingStub stub(GrpcChannelFactory channels) {
        return AccountServiceGrpc.newBlockingStub(channels.createChannel("auth-service"),
                ChannelBuilderOptions.defaults().withInterceptors(List.of(new BearerTokenAuthenticationInterceptor(() -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication instanceof JwtAuthenticationToken ){
                        return ((JwtAuthenticationToken) authentication).getToken().getTokenValue();
                    }else {
                        throw new IllegalStateException("Authentication is not an instance of JwtAuthenticationToken");
                    }
                }))));
    }
}
