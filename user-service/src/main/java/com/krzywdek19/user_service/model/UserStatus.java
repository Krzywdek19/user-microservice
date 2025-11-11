package com.krzywdek19.user_service.model;

public enum UserStatus {
    PENDING, // WAITING_FOR_EMAIL_VERIFICATION
    ACTIVE, // VERIFIED AND USABLE
    LOCKED // BLOCKED BY ADMIN
}
