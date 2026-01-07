package com.krzywdek19.workout_service.model.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkoutSessionDto(
        UUID id,
        UUID workoutTemplateId,
        String status,
        Instant startedAt,
        Instant finishedAt,
        List<ExerciseSessionDto> exercises
) {}
