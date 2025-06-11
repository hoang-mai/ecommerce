package com.example.edu.school.auth.service;

public interface JwtService {
    String generateAccessToken(String email);

    String generateRefreshToken(String email);

    String extractEmailAccessToken(String token);

    boolean validateAccessToken(String token, String email);

    boolean validateRefreshToken(String token, String email);
}
