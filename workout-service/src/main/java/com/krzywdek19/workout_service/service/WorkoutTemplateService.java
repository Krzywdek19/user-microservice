package com.krzywdek19.workout_service.service;

import com.krzywdek19.workout_service.model.dto.WorkoutTemplateDto;
import com.krzywdek19.workout_service.model.request.CreateWorkoutTemplateRequest;

import java.util.UUID;

public interface WorkoutTemplateService {
    WorkoutTemplateDto createWorkout(String userEmail, UUID planId, CreateWorkoutTemplateRequest request);
    WorkoutTemplateDto getWorkout(String userEmail, UUID workoutTemplateId);
    void deleteWorkout(String userEmail, UUID workoutTemplateId);
}
