package com.krzywdek19.workout_service.repository;

import com.krzywdek19.workout_service.model.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, UUID> {
    Optional<WorkoutSession> findByUserEmailAndFinishedAtIsNull(String userEmail);
}

