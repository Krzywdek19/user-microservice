package com.krzywdek19.user_service.service.impl;

import com.krzywdek19.user_service.dto.request.*;
import com.krzywdek19.user_service.dto.response.TokenResponse;
import com.krzywdek19.user_service.dto.response.UserResponse;
import com.krzywdek19.user_service.exception.EmailTakenException;
import com.krzywdek19.user_service.mapper.UserMapper;
import com.krzywdek19.user_service.model.User;
import com.krzywdek19.user_service.model.UserStatus;
import com.krzywdek19.user_service.repository.UserRepository;
import com.krzywdek19.user_service.service.AuthService;
import com.krzywdek19.user_service.service.EmailVerificationService;
import com.krzywdek19.user_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

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

    }

    @Override
    public TokenResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public TokenResponse refreshToken(RefreshRequest request) {
        return null;
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
