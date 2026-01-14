package com.krzywdek19.workout_service.utils;

import com.krzywdek19.workout_service.model.SetSession;
import com.krzywdek19.workout_service.model.dto.SetSessionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SetSessionMapper {
    SetSessionDto toDto(SetSession setSession);
}
