package com.krzywdek19.user_service.service.impl;

import com.krzywdek19.user_service.config.JwtProperties;
import com.krzywdek19.user_service.dto.request.*;
import com.krzywdek19.user_service.dto.response.TokenResponse;
import com.krzywdek19.user_service.exception.*;
import com.krzywdek19.user_service.model.User;
import com.krzywdek19.user_service.model.UserStatus;
import com.krzywdek19.user_service.repository.UserRepository;
import com.krzywdek19.user_service.security.LoginRateLimiter;
import com.krzywdek19.user_service.service.EmailVerificationService;
import com.krzywdek19.user_service.service.JwtService;
import com.krzywdek19.user_service.service.ResetPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private LoginRateLimiter loginRateLimiter;
    @Mock
    private JwtService jwtService;
    @Mock
    private ResetPasswordService resetPasswordService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("test@example.com", "password");
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .status(UserStatus.PENDING)
                .build();
    }

    @Test
    void register_shouldRegisterUser_whenEmailIsNotTaken() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        var result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(user.getEmail(), result.email());
        verify(emailVerificationService, times(1)).createAndSendVerificationToken(user);
    }

    @Test
    void register_shouldThrowEmailTakenException_whenEmailIsTaken() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        // Act & Assert
        assertThrows(EmailTakenException.class, () -> authService.register(registerRequest));
    }

    @Test
    void verifyEmail_shouldCallEmailVerificationService() {
        // Arrange
        var request = new VerifyRequest("token");

        // Act
        authService.verifyEmail(request);

        // Assert
        verify(emailVerificationService, times(1)).verify("token");
    }

    @Test
    void login_shouldReturnTokenResponse_whenCredentialsAreValidAndUserIsActive() {
        // Arrange
        var loginRequest = new LoginRequest("test@example.com", "password");
        user.setStatus(UserStatus.ACTIVE);
        when(loginRateLimiter.isBlocked(loginRequest.email())).thenReturn(false);
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");
        when(jwtProperties.accessDuration()).thenReturn(3600L);

        // Act
        var result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Bearer", result.tokenType());
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        assertEquals(3600L, result.expiresIn());
        verify(loginRateLimiter, times(1)).resetAttempts(loginRequest.email());
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenTooManyLoginAttempts() {
        // Arrange
        var loginRequest = new LoginRequest("test@example.com", "password");
        when(loginRateLimiter.isBlocked(loginRequest.email())).thenReturn(true);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenUserNotFound() {
        // Arrange
        var loginRequest = new LoginRequest("test@example.com", "password");
        when(loginRateLimiter.isBlocked(loginRequest.email())).thenReturn(false);
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenPasswordIsIncorrect() {
        // Arrange
        var loginRequest = new LoginRequest("test@example.com", "password");
        when(loginRateLimiter.isBlocked(loginRequest.email())).thenReturn(false);
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
        verify(loginRateLimiter, times(1)).recordFailedAttempt(loginRequest.email());
    }

    @Test
    void login_shouldThrowAccountIsNotActiveException_whenUserIsPending() {
        // Arrange
        var loginRequest = new LoginRequest("test@example.com", "password");
        user.setStatus(UserStatus.PENDING);
        when(loginRateLimiter.isBlocked(loginRequest.email())).thenReturn(false);
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())).thenReturn(true);

        // Act & Assert
        assertThrows(AccountIsNotActiveException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_shouldThrowAccountIsNotActiveException_whenUserIsBlocked() {
        // Arrange
        var loginRequest = new LoginRequest("test@example.com", "password");
        user.setStatus(UserStatus.LOCKED);
        when(loginRateLimiter.isBlocked(loginRequest.email())).thenReturn(false);
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())).thenReturn(true);

        // Act & Assert
        assertThrows(AccountIsNotActiveException.class, () -> authService.login(loginRequest));
    }

    @Test
    void refreshToken_shouldReturnNewTokenResponse_whenRefreshTokenIsValid() {
        // Arrange
        var refreshRequest = new RefreshRequest("validRefreshToken");
        when(jwtService.extractUsername("validRefreshToken")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("validRefreshToken", user)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("newRefreshToken");
        when(jwtProperties.accessDuration()).thenReturn(3600L);

        // Act
        TokenResponse result = authService.refreshToken(refreshRequest);

        // Assert
        assertNotNull(result);
        assertEquals("newAccessToken", result.accessToken());
        assertEquals("newRefreshToken", result.refreshToken());
    }

    @Test
    void refreshToken_shouldThrowInvalidRefreshTokenException_whenUserNotFound() {
        // Arrange
        var refreshRequest = new RefreshRequest("validRefreshToken");
        when(jwtService.extractUsername("validRefreshToken")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(refreshRequest));
    }

    @Test
    void refreshToken_shouldThrowInvalidRefreshTokenException_whenTokenIsInvalid() {
        // Arrange
        var refreshRequest = new RefreshRequest("invalidRefreshToken");
        when(jwtService.extractUsername("invalidRefreshToken")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("invalidRefreshToken", user)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(refreshRequest));
    }

    @Test
    void forgotPassword_shouldCallResetPasswordService_whenUserExists() {
        // Arrange
        var request = new ForgotPasswordRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        authService.forgotPassword(request);

        // Assert
        verify(resetPasswordService, times(1)).createAndSendResetToken(user);
    }

    @Test
    void forgotPassword_shouldNotCallResetPasswordService_whenUserDoesNotExist() {
        // Arrange
        var request = new ForgotPasswordRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act
        authService.forgotPassword(request);

        // Assert
        verify(resetPasswordService, never()).createAndSendResetToken(any(User.class));
    }

    @Test
    void resetPassword_shouldCallResetPasswordService() {
        // Arrange
        var request = new ResetPasswordRequest("token", "newPassword");

        // Act
        authService.resetPassword(request);

        // Assert
        verify(resetPasswordService, times(1)).resetPassword("token", "newPassword");
    }
}
