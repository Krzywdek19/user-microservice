package com.krzywdek19.user_service.service.impl;

import com.krzywdek19.user_service.config.JwtProperties;
import com.krzywdek19.user_service.dto.request.*;
import com.krzywdek19.user_service.dto.response.TokenResponse;
import com.krzywdek19.user_service.dto.response.UserResponse;
import com.krzywdek19.user_service.exception.AccountIsNotActiveException;
import com.krzywdek19.user_service.exception.EmailTakenException;
import com.krzywdek19.user_service.exception.InvalidCredentialsException;
import com.krzywdek19.user_service.exception.InvalidRefreshTokenException;
import com.krzywdek19.user_service.mapper.UserMapper;
import com.krzywdek19.user_service.model.User;
import com.krzywdek19.user_service.model.UserStatus;
import com.krzywdek19.user_service.repository.UserRepository;
import com.krzywdek19.user_service.service.AuthService;
import com.krzywdek19.user_service.service.EmailVerificationService;
import com.krzywdek19.user_service.service.JwtService;
import com.krzywdek19.user_service.service.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final JwtService jwtService;
    private final ResetPasswordService resetPasswordService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByEmail(registerRequest.email())) {
            throw new EmailTakenException("Email already in use");
        }
        var user = User.builder()
                .email(registerRequest.email())
                .passwordHash(passwordEncoder.encode(registerRequest.password()))
                .status(UserStatus.PENDING)
                .build();
        var createdUser = userRepository.save(user);
        emailVerificationService.createAndSendVerificationToken(createdUser);
        return UserMapper.toUserResponse(createdUser);
    }

    @Override
    public void verifyEmail(VerifyRequest request) {
        emailVerificationService.verify(request.token());
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            if (user.getStatus() == UserStatus.PENDING) {
                throw new AccountIsNotActiveException("Account is pending for activation");
            }
            throw new AccountIsNotActiveException("Account is blocked");
        }

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        long expiresIn = jwtProperties.accessDuration(); 

        return new TokenResponse("Bearer", accessToken, expiresIn, refreshToken);
    }


    @Override
    public TokenResponse refreshToken(RefreshRequest request) {
        var refreshToken = request.refreshToken();
        var username = jwtService.extractUsername(refreshToken);
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
        if(!jwtService.isTokenValid(refreshToken, user)){
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
        var newAccessToken = jwtService.generateAccessToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);
        long expiresIn = jwtProperties.accessDuration();

        return new TokenResponse("Bearer", newAccessToken, expiresIn, newRefreshToken);
    }

    @Override
    public void logout(String accessToken) {

    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

    }
}
