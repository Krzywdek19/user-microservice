package com.krzywdek19.workout_service.exceptions;

import java.util.UUID;

public class ResourceOwnershipException extends RuntimeException {
    public ResourceOwnershipException(String resourceName, UUID resourceId, String userEmail) {
        super(resourceName + " with id: " + resourceId + " does not belong to user " + userEmail);
    }
}
