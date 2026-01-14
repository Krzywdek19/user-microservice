package com.krzywdek19.workout_service.exceptions;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, UUID resourceId) {
        super(resourceName + " with id: " + resourceId + " not found");
    }
}
