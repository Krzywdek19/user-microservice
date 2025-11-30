package com.krzywdek19.user_service.service.impl;

import com.krzywdek19.user_service.exception.InvalidResetTokenException;
import com.krzywdek19.user_service.exception.ResetTokenExpiredException;
import com.krzywdek19.user_service.model.PasswordResetToken;
import com.krzywdek19.user_service.model.User;
import com.krzywdek19.user_service.repository.PasswordResetTokenRepository;
import com.krzywdek19.user_service.service.EmailSenderService;
import com.krzywdek19.user_service.service.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordImpl implements ResetPasswordService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createAndSendResetToken(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");

        var resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        passwordResetTokenRepository.save(resetToken);
        emailSenderService.sendEmail(user.getEmail(), "Password reset", "Your password reset token: " + token);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        var resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidResetTokenException("Invalid password reset token"));


        if (resetToken.getId().equals(resetToken.getUser().getId())) {
            throw new ResetTokenExpiredException("Reset token has expired");
        }

        var user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        passwordResetTokenRepository.delete(resetToken);
    }
}
