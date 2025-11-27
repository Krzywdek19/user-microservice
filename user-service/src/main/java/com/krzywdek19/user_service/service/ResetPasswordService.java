package com.krzywdek19.user_service.service;

import com.krzywdek19.user_service.dto.request.ForgotPasswordRequest;

public interface ResetPasswordService {
    void createAndSendResetToken(ForgotPasswordRequest request);
}
