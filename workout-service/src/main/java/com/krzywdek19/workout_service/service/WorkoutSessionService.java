package com.krzywdek19.workout_service.service;

import com.krzywdek19.workout_service.model.dto.WorkoutSessionDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface WorkoutSessionService {
    @Transactional
    WorkoutSessionDto startWorkout(String userEmail, UUID workoutTemplateId);
    WorkoutSessionDto getSession(String userEmail, UUID sessionId);
    WorkoutSessionDto getActiveSession(String userEmail);
    void finishWorkout(String userEmail, UUID sessionId);
    void abandonWorkout(String userEmail, UUID sessionId);
}

