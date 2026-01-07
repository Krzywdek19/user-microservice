package com.krzywdek19.workout_service.service.impl;

import com.krzywdek19.workout_service.exceptions.ActiveWorkoutSessionException;
import com.krzywdek19.workout_service.model.*;
import com.krzywdek19.workout_service.model.dto.WorkoutSessionDto;
import com.krzywdek19.workout_service.model.enums.WorkoutSessionStatus;
import com.krzywdek19.workout_service.repository.ExerciseSessionRepository;
import com.krzywdek19.workout_service.repository.SetSessionRepository;
import com.krzywdek19.workout_service.repository.WorkoutSessionRepository;
import com.krzywdek19.workout_service.repository.WorkoutTemplateRepository;
import com.krzywdek19.workout_service.service.SetSessionService;
import com.krzywdek19.workout_service.service.WorkoutSessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.UUID;

/**
 * Service implementing workout session lifecycle operations.
 */
@RequiredArgsConstructor
@Service
public class WorkoutSessionServiceImpl implements WorkoutSessionService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final ExerciseSessionRepository exerciseSessionRepository;
    private final SetSessionRepository setSessionRepository;

    /**
     * Starts a workout session for the given user and workout template.
     *
     * @param userEmail         user identifier (email)
     * @param workoutTemplateId template id to start the session from
     * @return DTO with created session details
     */
    @Override
    @Transactional
    public WorkoutSessionDto startWorkout(String userEmail, UUID workoutTemplateId) {
        ensureNoActiveSession(userEmail);

        var workoutTemplate = fetchWorkoutTemplate(workoutTemplateId);

        var savedSession = createAndSaveWorkoutSession(userEmail, workoutTemplate);

        createExerciseAndSetSessions(savedSession, workoutTemplate);

        return new WorkoutSessionDto(savedSession.getId(), workoutTemplateId, savedSession.getStatus().name(),null, null, null);
    }

    @Override
    public WorkoutSessionDto getSession(String userEmail, UUID sessionId) {
        return null;
    }

    @Override
    public WorkoutSessionDto getActiveSession(String userEmail) {
        return null;
    }

    @Override
    public void finishWorkout(String userEmail, UUID sessionId) {

    }

    @Override
    public void abandonWorkout(String userEmail, UUID sessionId) {

    }

    /**
     * Ensures the user has no active (unfinished) workout session.
     *
     * @param userEmail user identifier
     * @throws ActiveWorkoutSessionException if an active session exists
     */
    private void ensureNoActiveSession(String userEmail) {
        if (workoutSessionRepository.findByUserEmailAndFinishedAtIsNull(userEmail).isPresent()) {
            throw new ActiveWorkoutSessionException(userEmail);
        }
    }

    /**
     * Fetches workout template by id or throws EntityNotFoundException.
     *
     * @param workoutTemplateId template id
     * @return found WorkoutTemplate
     */
    private WorkoutTemplate fetchWorkoutTemplate(UUID workoutTemplateId) {
        return workoutTemplateRepository.findById(workoutTemplateId)
                .orElseThrow(() -> new EntityNotFoundException("Workout template not found"));
    }

    /**
     * Creates and persists a new WorkoutSession.
     *
     * @param userEmail       user identifier
     * @param workoutTemplate template used for the session
     * @return persisted WorkoutSession
     */
    private WorkoutSession createAndSaveWorkoutSession(String userEmail, WorkoutTemplate workoutTemplate) {
        var session = WorkoutSession.builder()
                .userEmail(userEmail)
                .workoutTemplate(workoutTemplate)
                .status(WorkoutSessionStatus.IN_PROGRESS)
                .build();
        return workoutSessionRepository.save(session);
    }

    /**
     * Creates ExerciseSession and SetSession records based on the workout template.
     *
     * @param savedSession    persisted WorkoutSession
     * @param workoutTemplate source template with exercises and sets count
     */
    private void createExerciseAndSetSessions(WorkoutSession savedSession, WorkoutTemplate workoutTemplate) {
        var exercises = workoutTemplate.getExercises().stream()
                .sorted(Comparator.comparingInt(ExerciseTemplate::getOrderIndex))
                .toList();

        for (ExerciseTemplate template : exercises) {
            var exerciseSession = ExerciseSession.builder()
                    .workoutSession(savedSession)
                    .name(template.getName())
                    .orderIndex(template.getOrderIndex())
                    .build();
            var savedExerciseSession = exerciseSessionRepository.save(exerciseSession);

            for (int i = 0; i < template.getSetsCount(); i++) {
                var setSession = SetSession.builder()
                        .exerciseSession(savedExerciseSession)
                        .orderIndex(i + 1)
                        .completed(false)
                        .build();
                setSessionRepository.save(setSession);
            }
        }
    }
}
