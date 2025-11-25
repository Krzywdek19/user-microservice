package com.krzywdek19.user_service.service;

import com.krzywdek19.user_service.model.User;

public interface EmailVerificationService {
    void createAndSendVerificationToken(User user);
    void verify(String token);
}
