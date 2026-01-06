package com.krzywdek19.workout_service.repository;

import com.krzywdek19.workout_service.model.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, UUID> {
}

