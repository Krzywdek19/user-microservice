package com.krzywdek19.workout_service.utils;

import com.krzywdek19.workout_service.model.WorkoutTemplate;
import com.krzywdek19.workout_service.model.dto.WorkoutTemplateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ExerciseTemplateMapper.class})
public interface WorkoutTemplateMapper {
    @Mapping(target = "exercises", source = "exercises")
    WorkoutTemplateDto toDto(WorkoutTemplate workoutTemplate);
}

