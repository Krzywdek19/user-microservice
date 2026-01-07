package com.krzywdek19.workout_service.service;

import com.krzywdek19.workout_service.model.dto.SetSessionDto;
import com.krzywdek19.workout_service.model.request.UpdateSetRequest;

import java.util.UUID;

public interface SetSessionService {
    SetSessionDto updateSet(String userEmail, UUID setSessionId, UpdateSetRequest request);
}

