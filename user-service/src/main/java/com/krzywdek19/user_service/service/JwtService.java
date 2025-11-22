package com.krzywdek19.user_service.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateAccessToken(UserDetails user);
    String generateRefreshToken(UserDetails user);
    boolean isTokenValid(String token, UserDetails user);
    String extractUsername(String token);
}
