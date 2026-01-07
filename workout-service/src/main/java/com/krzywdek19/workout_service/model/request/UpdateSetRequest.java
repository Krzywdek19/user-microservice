package com.krzywdek19.workout_service.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateSetRequest(
        @NotNull @Min(0) Integer reps,
        @NotNull @DecimalMin("0.0") BigDecimal weight,
        Integer rir,
        @NotNull Boolean completed
) {}
