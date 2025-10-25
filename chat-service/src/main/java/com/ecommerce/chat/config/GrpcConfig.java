package com.ecommerce.chat.config;

import com.example.app.chat.user.UserServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;


@Configuration
public class GrpcConfig {

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub stub(GrpcChannelFactory channels) {
        return UserServiceGrpc.newBlockingStub(channels.createChannel("user-service"));
    }
}
