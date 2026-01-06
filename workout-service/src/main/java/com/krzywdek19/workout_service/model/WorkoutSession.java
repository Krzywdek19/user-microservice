package com.krzywdek19.workout_service.model;

import com.krzywdek19.workout_service.model.enums.WorkoutSessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workout_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSession {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_template_id", nullable = false)
    private WorkoutTemplate workoutTemplate;
    @Column(nullable = false)
    private String userEmail;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutSessionStatus status;
    @Column(nullable = false)
    private Instant startedAt;
    private Instant finishedAt;
    @OneToMany(
            mappedBy = "workoutSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ExerciseSession> exercises = new ArrayList<>();
}
