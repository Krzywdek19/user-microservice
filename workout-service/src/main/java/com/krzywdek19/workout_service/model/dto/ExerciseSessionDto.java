package com.krzywdek19.workout_service.model.dto;

import java.util.List;
import java.util.UUID;

public record ExerciseSessionDto(
        UUID id,
        String name,
        Integer orderIndex,
        Integer setsCount,
        List<SetSessionDto> sets
) {}
