package com.krzywdek19.workout_service.model.dto;

import java.util.List;
import java.util.UUID;

public record WorkoutTemplateDto(
        UUID id,
        String name,
        Integer orderIndex,
        List<ExerciseTemplateDto> exercises
) {}
