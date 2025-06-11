package com.example.edu.school.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImp implements JwtService{

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret-key-access-token}")
    private String secretKeyAccessToken;

    @Value("${jwt.secret-key-refresh-token}")
    private String secretKeyRefreshToken;

    @Value("${jwt.expiration-access-token}")
    private long expirationAccessToken;

    @Value("${jwt.expiration-refresh-token}")
    private long expirationRefreshToken;

    @Override
    public String generateAccessToken(String email) {
        String accessToken= generateToken(email,secretKeyAccessToken, expirationAccessToken);
        storeTokenInRedis(email, accessToken);
        return accessToken;
    }

    @Override
    public String generateRefreshToken(String email) {
        return generateToken(email,secretKeyRefreshToken, expirationRefreshToken);
    }

    private String generateToken(String email,String secretKey, long expiration) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(secretKey), SignatureAlgorithm.HS256)
                .compact();

    }

    private Key getSignKey(String secretKey) {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

    private <T> T extractClaim(String token,String secretKey, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token,String secretKey) {
        return !extractClaim(token, secretKey, Claims::getExpiration).before(new Date());
    }


    private String extractEmail(String token,String secretKey) {
        return extractClaim(token,secretKey, Claims::getSubject);
    }

    @Override
    public String extractEmailAccessToken(String token) {
        return extractEmail(token,secretKeyAccessToken);
    }

    @Override
    public boolean validateAccessToken(String token, String email) {
        return extractEmail(token,secretKeyAccessToken).equals(email) && isTokenExpired(token, secretKeyAccessToken);
    }

    @Override
    public boolean validateRefreshToken(String token, String email) {
        return extractEmail(token,secretKeyRefreshToken).equals(email) && isTokenExpired(token, secretKeyRefreshToken);
    }

    private void storeTokenInRedis(String email, String token) {
        System.out.println("Storing token in Redis: " + token);
        redisTemplate.opsForValue().set(email, token, expirationAccessToken, TimeUnit.MILLISECONDS);
    }
}
