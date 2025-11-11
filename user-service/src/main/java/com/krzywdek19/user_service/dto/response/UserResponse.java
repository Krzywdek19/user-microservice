package com.krzywdek19.user_service.dto.response;

import com.krzywdek19.user_service.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse (
        UUID id,
        String email,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
