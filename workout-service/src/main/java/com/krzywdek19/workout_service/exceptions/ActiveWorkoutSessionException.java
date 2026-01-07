package com.krzywdek19.workout_service.exceptions;

public class ActiveWorkoutSessionException extends RuntimeException {
    public ActiveWorkoutSessionException(String userEmail) {
        super("User already has an active workout session: " + userEmail);
    }
}
