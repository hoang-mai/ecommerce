package com.example.edu.school.user.config;

import com.example.edu.school.auth.AccountServiceGrpc;
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
