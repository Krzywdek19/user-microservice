package com.krzywdek19.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public abstract class BaseToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    @ManyToOne(optional = false)
    protected User user;

    @Column(nullable = false, unique = true)
    protected String token;

    @Column(nullable = false)
    protected Instant expiresAt;
}