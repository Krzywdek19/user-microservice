package com.krzywdek19.user_service.exception;

public class ResetTokenExpiredException extends RuntimeException {
    public ResetTokenExpiredException(String message) {
        super(message);
    }
}
