package com.ecommerce.user.config;

import com.ecommerce.auth.AccountServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.security.config.Customizer;


@Configuration
public class GrpcConfig {

    @GlobalServerInterceptor
    @Bean
    public AuthenticationProcessInterceptor jwtSecurityFilterChain(GrpcSecurity grpc) throws Exception {
        return grpc.authorizeRequests(requestMapperConfigurer ->
                        requestMapperConfigurer
                                .methods("user.UserService/CreateUser").permitAll()
                                .methods("user.UserService/DeleteUser").permitAll()
                                .allRequests().authenticated())
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer ->
                        oAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults())).build();
    }

}
