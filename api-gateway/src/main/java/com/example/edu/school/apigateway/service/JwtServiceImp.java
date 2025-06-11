package com.example.edu.school.apigateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtServiceImp implements JwtService{

    @Value("${jwt.secret-key-access-token}")
    private String secretKeyAccessToken;

    private Key getSignKey() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKeyAccessToken);
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return !extractClaim(token, Claims::getExpiration).before(new Date());
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean validateToken(String token) {
        return isTokenExpired(token);
    }

}
