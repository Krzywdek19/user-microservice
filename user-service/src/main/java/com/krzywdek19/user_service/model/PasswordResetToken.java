package com.krzywdek19.user_service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "password_reset_tokens")
@NoArgsConstructor
@SuperBuilder
@Getter
public class PasswordResetToken extends BaseToken {
}