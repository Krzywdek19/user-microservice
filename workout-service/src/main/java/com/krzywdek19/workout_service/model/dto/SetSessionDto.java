package com.krzywdek19.workout_service.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SetSessionDto(
        UUID id,
        Integer orderIndex,
        Integer reps,
        BigDecimal weight,
        Integer rir,
        boolean completed
) {}
