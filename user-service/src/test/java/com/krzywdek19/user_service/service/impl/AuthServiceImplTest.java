package com.krzywdek19.user_service.service.impl;


import com.krzywdek19.user_service.config.JwtProperties;
import com.krzywdek19.user_service.dto.request.*;
import com.krzywdek19.user_service.exception.AccountIsNotActiveException;
import com.krzywdek19.user_service.exception.EmailTakenException;
import com.krzywdek19.user_service.exception.InvalidCredentialsException;
import com.krzywdek19.user_service.exception.InvalidRefreshTokenException;
import com.krzywdek19.user_service.model.User;
import com.krzywdek19.user_service.model.UserStatus;
import com.krzywdek19.user_service.repository.UserRepository;
import com.krzywdek19.user_service.service.JwtService;
import com.krzywdek19.user_service.service.ResetPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailVerificationServiceImpl emailVerificationService;
    @Mock
    private JwtService jwtService;
    @Mock
    private ResetPasswordService resetPasswordService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProperties jwtProperties;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                emailVerificationService,
                jwtService,
                resetPasswordService,
                passwordEncoder,
                jwtProperties
        );
    }

    @Test
    void register_shouldCreateUserAndSendVerificationEmail() {
        //given
        var request = new RegisterRequest("test@test.com", "password123");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        var savedUser = User.builder()
                .id(java.util.UUID.randomUUID())
                .email("test@test.com")
                .passwordHash("hashedPassword")
                .status(UserStatus.PENDING)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //when
        var response = authService.register(request);

        //then
        assertEquals(savedUser.getId(), response.id());
        assertEquals(savedUser.getEmail(), response.email());
        assertEquals(savedUser.getStatus().name(), response.status().name());

        verify(userRepository).existsByEmail("test@test.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(emailVerificationService).createAndSendVerificationToken(savedUser);
    }

    @Test
    void register_shouldThrowEmailTakenException_whenEmailAlreadyExists() {
        //given
        var request = new RegisterRequest("test@test.com", "password123");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        //when + then
        assertThrows(EmailTakenException.class, () -> authService.register(request));

        verify(userRepository).existsByEmail("test@test.com");
        verifyNoMoreInteractions(userRepository, emailVerificationService, passwordEncoder);
    }

    @Test
    void login_shouldReturnTokenResponse_whenCredentialsAreValidAndUserIsActive() {
        //given
        var request = new LoginRequest("test@test.com", "password123");
        var user = User.builder()
                .id(java.util.UUID.randomUUID())
                .email("test@test.com")
                .passwordHash("hashedPassword")
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");
        when(jwtProperties.accessDuration()).thenReturn(3600L);

        //when
        var tokenResponse = authService.login(request);

        //then
        assertEquals("Bearer", tokenResponse.tokenType());
        assertEquals("accessToken", tokenResponse.accessToken());
        assertEquals("refreshToken", tokenResponse.refreshToken());
        assertEquals(3600L, tokenResponse.expiresIn());

        verify(userRepository).findByEmail("test@test.com");
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenEmailNotFound() {
        //given
        var request = new LoginRequest("test@test.com", "password123");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        //when + then
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));

        verify(userRepository).findByEmail("test@test.com");
        verifyNoMoreInteractions(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void login_shouldThrowInvalidCredentials_whenPasswordDoesNotMatch() {
        // given
        var request = new LoginRequest("test@test.com", "wrong");
        var user = User.builder()
                .email("test@test.com")
                .passwordHash("hashedPassword")
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashedPassword")).thenReturn(false);

        // when + then
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));

        verify(passwordEncoder).matches("wrong", "hashedPassword");
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void login_shouldThrowAccountIsNotActiveException_whenUserIsPending() {
        // given
        var request = new LoginRequest("test@test.com", "password123");
        var user = User.builder()
                .email("test@test.com")
                .passwordHash("hashedPassword")
                .status(UserStatus.PENDING)
                .build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        // when + then
        assertThrows(AccountIsNotActiveException.class, () -> authService.login(request));
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void refreshToken_shouldReturnNewTokens_whenRefreshTokenIsValid() {
        // given
        var request = new RefreshRequest("valid-refresh-token");

        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("test@test.com");

        var user = User.builder()
                .email("test@test.com")
                .passwordHash("hashed-password")
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("valid-refresh-token", user)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("new-access");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh");
        when(jwtProperties.accessDuration()).thenReturn(3600L);

        // when
        var response = authService.refreshToken(request);

        // then
        assertEquals("Bearer", response.tokenType());
        assertEquals("new-access", response.accessToken());
        assertEquals("new-refresh", response.refreshToken());
        assertEquals(3600L, response.expiresIn());

        verify(jwtService).extractUsername("valid-refresh-token");
        verify(userRepository).findByEmail("test@test.com");
        verify(jwtService).isTokenValid("valid-refresh-token", user);
        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void refreshToken_shouldThrowInvalidRefreshToken_whenUserNotFound() {
        var request = new RefreshRequest("some-token");

        when(jwtService.extractUsername("some-token")).thenReturn("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(request));
    }

    @Test
    void forgotPassword_shouldCallResetService_whenUserExists() {
        var request = new ForgotPasswordRequest("test@test.com");
        var user = User.builder().email("test@test.com").build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        authService.forgotPassword(request);

        verify(resetPasswordService).createAndSendResetToken(user);
    }

    @Test
    void forgotPassword_shouldDoNothing_whenUserDoesNotExist() {
        var request = new ForgotPasswordRequest("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        authService.forgotPassword(request);

        verifyNoInteractions(resetPasswordService);
    }
}
