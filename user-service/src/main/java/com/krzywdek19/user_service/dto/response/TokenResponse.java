package com.krzywdek19.user_service.dto.response;

public record TokenResponse(
        String tokenType,
        String accessToken,
        long expiresIn,
        String refreshToken
) {
}
