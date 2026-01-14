package com.krzywdek19.workout_service.utils;

import com.krzywdek19.workout_service.model.TrainingPlan;
import com.krzywdek19.workout_service.model.dto.TrainingPlanDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WorkoutTemplateMapper.class})
public interface TrainingPlanMapper {
    @Mapping(target = "workouts", source = "workouts")
    TrainingPlanDto toDto(TrainingPlan trainingPlan);
}

