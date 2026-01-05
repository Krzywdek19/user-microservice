package com.krzywdek19.user_service.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;

public interface JwtService {
    String generateAccessToken(UserDetails user);
    String generateRefreshToken(UserDetails user);
    boolean isTokenValid(String token, UserDetails user);
    String extractUsername(String token);
    Date extractExpiration(String token);
    String extractJti(String token);
    default Duration getRemainingValidity(String token) {
        Date expiration = extractExpiration(token);
        long millis = expiration.getTime() - System.currentTimeMillis();
        return Duration.ofMillis(Math.max(millis, 0));
    }
}
