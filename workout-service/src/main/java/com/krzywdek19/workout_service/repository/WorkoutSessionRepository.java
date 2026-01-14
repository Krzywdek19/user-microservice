package com.krzywdek19.workout_service.repository;

import com.krzywdek19.workout_service.model.WorkoutSession;
import com.krzywdek19.workout_service.model.enums.WorkoutSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, UUID> {
    Optional<WorkoutSession> findByUserEmailAndFinishedAtIsNull(String userEmail);
    Optional<WorkoutSession> findByUserEmailAndStatus(String userEmail, WorkoutSessionStatus status);
    boolean existsByUserEmailAndStatusIsNot(String userEmail, WorkoutSessionStatus status);
}

