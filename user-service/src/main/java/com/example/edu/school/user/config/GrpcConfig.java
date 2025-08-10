package com.example.edu.school.user.config;

import com.example.edu.school.auth.AccountServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Configuration
public class GrpcConfig {

    @Bean
    @RequestScope
    public AccountServiceGrpc.AccountServiceBlockingStub stub(GrpcChannelFactory channels) {
        ChannelBuilderOptions channelBuilderOptions = ChannelBuilderOptions.defaults().withInterceptors(
                List.of(new BearerTokenAuthenticationInterceptor(() -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
                        return jwtAuthenticationToken.getToken().getTokenValue();
                    }
                    return null;
                })));
        ManagedChannel managedChannel = channels.createChannel("auth-service", channelBuilderOptions);
        return AccountServiceGrpc.newBlockingStub(managedChannel);
    }
}
