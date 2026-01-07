package com.krzywdek19.workout_service.model.dto;

import java.util.List;
import java.util.UUID;

public record TrainingPlanDto(
        UUID id,
        String name,
        String status,
        List<WorkoutTemplateDto> workouts
) {}
