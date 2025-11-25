package com.krzywdek19.user_service.controller;

import com.krzywdek19.user_service.dto.request.*;
import com.krzywdek19.user_service.dto.response.TokenResponse;
import com.krzywdek19.user_service.dto.response.UserResponse;
import com.krzywdek19.user_service.exception.UnauthorizedException;
import com.krzywdek19.user_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        var userResponse = authService.register(request);
        var uri = URI.create("/users/" + userResponse.id());
        return ResponseEntity.created(uri).body(userResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid VerifyRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        var token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody @Valid RefreshRequest request) {
        var token = authService.refreshToken(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid token");
        }
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}

