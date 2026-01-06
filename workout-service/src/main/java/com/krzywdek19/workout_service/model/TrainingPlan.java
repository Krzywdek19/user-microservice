package com.krzywdek19.workout_service.model;

import com.krzywdek19.workout_service.model.enums.TrainingPlanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "training_plans")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TrainingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String userEmail;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingPlanStatus status;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;
    @OneToMany(
            mappedBy = "trainingPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WorkoutTemplate> workouts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
