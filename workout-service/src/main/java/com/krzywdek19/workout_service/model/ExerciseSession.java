package com.krzywdek19.workout_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exercise_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseSession {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_session_id", nullable = false)
    private WorkoutSession workoutSession;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_template_id", nullable = false)
    private ExerciseTemplate exerciseTemplate;
    @Column(nullable = false)
    private String name;
    @OneToMany(
            mappedBy = "exerciseSession",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SetSession> sets = new ArrayList<>();
    @Column(nullable = false)
    private int orderIndex;
    @Column(nullable = false)
    private int setsCount;
}

