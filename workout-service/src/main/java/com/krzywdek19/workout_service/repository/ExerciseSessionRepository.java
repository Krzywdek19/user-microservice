package com.krzywdek19.workout_service.repository;

import com.krzywdek19.workout_service.model.ExerciseSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExerciseSessionRepository extends JpaRepository<ExerciseSession, UUID> {
}
