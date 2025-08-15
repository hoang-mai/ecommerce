package com.example.app.chat.user.config;

import com.example.app.chat.auth.AccountServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;


@Configuration
public class GrpcConfig {

    @Bean
    public AccountServiceGrpc.AccountServiceBlockingStub stub(GrpcChannelFactory channels) {
        return AccountServiceGrpc.newBlockingStub(channels.createChannel("auth-service"));
    }
}
