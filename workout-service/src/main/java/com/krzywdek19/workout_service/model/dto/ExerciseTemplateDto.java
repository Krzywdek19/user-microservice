package com.krzywdek19.workout_service.model.dto;

import java.util.UUID;

public record ExerciseTemplateDto(
        UUID id,
        String name,
        int setsCount,
        int orderIndex,
        String notes
) {
}
