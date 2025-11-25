package com.krzywdek19.user_service.repository;

import com.krzywdek19.user_service.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {
    boolean existsByToken(String token);
}
