package com.krzywdek19.user_service.service;


import com.krzywdek19.user_service.model.User;

public interface ResetPasswordService {
    void createAndSendResetToken(User user);
    void resetPassword(String token, String newPassword);
}
