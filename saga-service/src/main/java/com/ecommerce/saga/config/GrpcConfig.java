package com.ecommerce.saga.config;

import com.ecommerce.auth.AccountServiceGrpc;
import com.ecommerce.user.UserServiceGrpc;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
@RequiredArgsConstructor
public class GrpcConfig {

    private final EurekaClient eurekaClient;

    private ManagedChannel createChannel(String serviceName){
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(serviceName.toUpperCase(), false);
        String address = instanceInfo.getIPAddr();
        int port = instanceInfo.getMetadata().get("grpc-port") != null ?
                Integer.parseInt(instanceInfo.getMetadata().get("grpc-port")) :
                instanceInfo.getPort();
        return ManagedChannelBuilder.forAddress(address, port)
                .usePlaintext()
                .build();
    }



    @Bean
    public AccountServiceGrpc.AccountServiceBlockingStub stubAuthService() {
        return AccountServiceGrpc.newBlockingStub(createChannel("auth-service"));
    }

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub stubUserService() {
        return UserServiceGrpc.newBlockingStub(createChannel("user-service"));
    }

}
