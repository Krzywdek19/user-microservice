package com.krzywdek19.workout_service.exceptions;

public class ActiveWorkoutSessionException extends RuntimeException {
    public ActiveWorkoutSessionException(String message) {
        super(message);
    }
}
