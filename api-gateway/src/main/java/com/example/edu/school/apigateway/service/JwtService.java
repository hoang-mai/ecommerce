package com.example.edu.school.apigateway.service;

public interface JwtService {
    String extractEmail(String token);

    boolean validateToken(String token);

}
