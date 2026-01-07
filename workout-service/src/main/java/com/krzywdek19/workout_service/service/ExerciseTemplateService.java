package com.krzywdek19.workout_service.service;

import com.krzywdek19.workout_service.model.dto.ExerciseTemplateDto;
import com.krzywdek19.workout_service.model.request.CreateExerciseTemplateRequest;
import com.krzywdek19.workout_service.model.request.UpdateExerciseTemplateRequest;

import java.util.UUID;

public interface ExerciseTemplateService {
    ExerciseTemplateDto addExercise(String userEmail, UUID workoutTemplateId, CreateExerciseTemplateRequest request);
    void updateExercise(String userEmail, UUID exerciseTemplateId, UpdateExerciseTemplateRequest request);
    void deleteExercise(String userEmail, UUID exerciseTemplateId);
}
