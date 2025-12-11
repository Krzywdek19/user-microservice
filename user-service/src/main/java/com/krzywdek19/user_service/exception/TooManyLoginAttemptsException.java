package com.krzywdek19.user_service.exception;

public class TooManyLoginAttemptsException extends RuntimeException {
    public TooManyLoginAttemptsException(String message) {
        super(message);
    }
}
