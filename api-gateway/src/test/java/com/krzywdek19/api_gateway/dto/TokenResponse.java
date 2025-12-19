package com.krzywdek19.api_gateway.dto;

public record TokenResponse(
        String tokenType,
        String accessToken,
        long expiresIn,
        String refreshToken
) {}
