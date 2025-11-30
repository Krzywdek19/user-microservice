package com.krzywdek19.user_service.service;

import com.krzywdek19.user_service.dto.request.*;
import com.krzywdek19.user_service.dto.response.TokenResponse;
import com.krzywdek19.user_service.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest registerRequest);
    void verifyEmail(VerifyRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refreshToken(RefreshRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
