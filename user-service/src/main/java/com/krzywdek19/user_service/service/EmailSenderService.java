package com.krzywdek19.user_service.service;

public interface EmailSenderService {
    void sendEmail(String to, String subject, String body);
}
