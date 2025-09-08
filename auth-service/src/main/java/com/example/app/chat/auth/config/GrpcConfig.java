package com.example.app.chat.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                                .methods("auth.AccountService/CreateAccount").permitAll()
                                .methods("auth.AccountService/DeleteAccount").permitAll()
                                .allRequests().authenticated()
                                )
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer ->
                        oAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults())).build();
    }
}
