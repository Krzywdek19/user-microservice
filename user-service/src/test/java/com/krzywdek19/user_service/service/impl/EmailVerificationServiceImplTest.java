package com.krzywdek19.user_service.service.impl;

import com.krzywdek19.user_service.exception.InvalidVerificationTokenException;
import com.krzywdek19.user_service.model.EmailVerificationToken;
import com.krzywdek19.user_service.model.User;
import com.krzywdek19.user_service.model.UserStatus;
import com.krzywdek19.user_service.repository.EmailVerificationTokenRepository;
import com.krzywdek19.user_service.repository.UserRepository;
import com.krzywdek19.user_service.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmailVerificationServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceImplTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private UserRepository userRepository;

    private EmailVerificationServiceImpl emailVerificationService;


    @BeforeEach
    void setUp() {
        emailVerificationService = new EmailVerificationServiceImpl(
                tokenRepository,
                emailSenderService,
                userRepository
        );
    }


    @Test
    void createAndSendVerificationToken_shouldCreateTokenAndSendEmail() {
        // Arrange
        var user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .status(UserStatus.PENDING)
                .build();

        var savedToken = EmailVerificationToken.builder()
                .id(UUID.randomUUID())
                .user(user)
                .token("generatedtoken123")
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(tokenRepository.save(any(EmailVerificationToken.class))).thenReturn(savedToken);

        // Act
        emailVerificationService.createAndSendVerificationToken(user);

        // Assert
        verify(tokenRepository).save(any(EmailVerificationToken.class));
        verify(emailSenderService).sendEmail(
                eq("test@example.com"),
                eq("Verify your email"),
                contains("Your verification token: ")
        );
    }


    @Test
    void verify_shouldActivateUserAndDeleteToken_whenTokenIsValid() {
        // Arrange
        String token = "validtoken123";
        var user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .status(UserStatus.PENDING)
                .build();

        var verificationToken = EmailVerificationToken.builder()
                .id(UUID.randomUUID())
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        emailVerificationService.verify(token);

        // Assert
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        verify(tokenRepository).findByToken(token);
        verify(userRepository).save(user);
        verify(tokenRepository).delete(verificationToken);
    }


    @Test
    void verify_shouldThrowException_whenTokenNotFound() {
        // Arrange
        String token = "invalidtoken123";
        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // Act & Assert
        InvalidVerificationTokenException exception = assertThrows(
                InvalidVerificationTokenException.class,
                () -> emailVerificationService.verify(token)
        );

        assertEquals("Invalid Verification Token", exception.getMessage());
        verify(tokenRepository).findByToken(token);
        verifyNoInteractions(userRepository);
        verify(tokenRepository, never()).delete(any());
    }


    @Test
    void verify_shouldThrowException_whenTokenIsExpired() {
        // Arrange
        String token = "expiredtoken123";
        var user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .status(UserStatus.PENDING)
                .build();

        var expiredToken = EmailVerificationToken.builder()
                .id(UUID.randomUUID())
                .user(user)
                .token(token)
                .expiresAt(Instant.now().minusSeconds(3600)) // Expired token
                .build();

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(expiredToken));

        // Act & Assert
        InvalidVerificationTokenException exception = assertThrows(
                InvalidVerificationTokenException.class,
                () -> emailVerificationService.verify(token)
        );

        assertEquals("Token has expired", exception.getMessage());
        verify(tokenRepository).findByToken(token);
        verifyNoInteractions(userRepository);
        verify(tokenRepository, never()).delete(any());
    }


    @Test
    void verify_shouldNotChangeAlreadyActiveUser_whenTokenIsValid() {
        // Arrange
        String token = "validtoken123";
        var user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .status(UserStatus.ACTIVE) // Already active
                .build();

        var verificationToken = EmailVerificationToken.builder()
                .id(UUID.randomUUID())
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        emailVerificationService.verify(token);

        // Assert
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        verify(tokenRepository).findByToken(token);
        verify(userRepository).save(user);
        verify(tokenRepository).delete(verificationToken);
    }
}
