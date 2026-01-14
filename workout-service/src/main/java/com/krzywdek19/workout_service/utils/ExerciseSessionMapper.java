package com.krzywdek19.workout_service.utils;

import com.krzywdek19.workout_service.model.ExerciseSession;
import com.krzywdek19.workout_service.model.dto.ExerciseSessionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SetSessionMapper.class})
public interface ExerciseSessionMapper {
    @Mapping(target = "sets", source = "sets" )
    ExerciseSessionDto toDto(ExerciseSession exerciseSession);
}
