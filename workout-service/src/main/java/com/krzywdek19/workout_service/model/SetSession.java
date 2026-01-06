package com.krzywdek19.workout_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "set_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetSession {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_session_id", nullable = false)
    private ExerciseSession exerciseSession;
    @Column(nullable = false)
    private int orderIndex;
    private Integer reps;
    private BigDecimal weight;
    private Integer rir;
    @Column(nullable = false)
    private boolean completed;
}
