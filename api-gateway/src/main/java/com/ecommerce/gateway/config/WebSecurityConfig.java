package com.ecommerce.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs-json/**",
                                "ws/**"
                        ).permitAll()
                        .pathMatchers("api/v1/user-view/search-address").permitAll()
                        .pathMatchers("/api/v1/push-subscription/unsubscribe").permitAll()
                        .pathMatchers("/api/v1/product-view/**").permitAll()
                        .pathMatchers("/api/v1/shop-view/**").permitAll()
                        .pathMatchers("/api/v1/review-view/**").permitAll()
                        .pathMatchers("/api/v1/review","/api/v1/review/**").hasAuthority("USER")
                        .pathMatchers("/api/v1/review-reply","/api/v1/review-reply/**").hasAuthority("OWNER")
                        .pathMatchers(HttpMethod.POST,"/api/v1/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST,"/api/v1/saga/register").permitAll()
                        .pathMatchers(HttpMethod.POST,"/api/v1/auth/refresh-token").permitAll()
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/auth/{userId}").hasAuthority("ADMIN")
                        .pathMatchers("/api/v1/address","/api/v1/address/**").hasAnyAuthority("USER", "OWNER")
                        .pathMatchers("/api/v1/user-verification").hasAnyAuthority("USER", "ADMIN")
                        .pathMatchers("/api/v1/user-verification/**").hasAnyAuthority("ADMIN")
                        .pathMatchers("api/v1/saga/{userVerificationId}/approve").hasAnyAuthority("ADMIN")
                        .pathMatchers("api/v1/shop-view").hasAuthority("OWNER")
                        .pathMatchers(HttpMethod.POST,"/api/v1/shop").hasAuthority("OWNER")
                        .pathMatchers(HttpMethod.PATCH,"/api/v1/shop/{shopId}").hasAuthority("OWNER")
                        .pathMatchers(HttpMethod.PATCH,"/api/v1/shop/{shopId}/status").hasAnyAuthority("ADMIN", "OWNER")
                        .pathMatchers("api/v1/order-view/**").hasAnyAuthority("OWNER", "USER")
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtSpec ->
                                jwtSpec.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("role");
        authoritiesConverter.setAuthorityPrefix("");

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
