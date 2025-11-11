package com.ecommerce.saga.config;

import com.ecommerce.auth.AccountServiceGrpc;
import com.ecommerce.user.UserServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {

    @Bean
    public AccountServiceGrpc.AccountServiceBlockingStub stubAuthService(GrpcChannelFactory channels) {
        return AccountServiceGrpc.newBlockingStub(channels.createChannel("auth-service"));
    }

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub stubUserService(GrpcChannelFactory channels) {
        return UserServiceGrpc.newBlockingStub(channels.createChannel("user-service"));
    }

}
