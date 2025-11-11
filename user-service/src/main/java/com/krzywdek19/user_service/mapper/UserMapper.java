package com.krzywdek19.user_service.mapper;

import com.krzywdek19.user_service.dto.response.UserResponse;
import com.krzywdek19.user_service.model.User;

public final class UserMapper {
    private UserMapper() {}

    public static UserResponse toUserResponse(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getStatus(), u.getCreatedAt(), u.getUpdatedAt());
    }
}