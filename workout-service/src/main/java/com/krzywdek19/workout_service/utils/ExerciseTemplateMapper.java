package com.krzywdek19.workout_service.utils;

import com.krzywdek19.workout_service.model.ExerciseTemplate;
import com.krzywdek19.workout_service.model.dto.ExerciseTemplateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseTemplateMapper {
    ExerciseTemplateDto toDto(ExerciseTemplate exerciseTemplate);
}

