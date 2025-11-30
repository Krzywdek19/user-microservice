package com.krzywdek19.user_service.service.impl;

import com.krzywdek19.user_service.exception.InvalidVerificationTokenException;
import com.krzywdek19.user_service.model.EmailVerificationToken;
import com.krzywdek19.user_service.model.User;
import com.krzywdek19.user_service.model.UserStatus;
import com.krzywdek19.user_service.repository.EmailVerificationTokenRepository;
import com.krzywdek19.user_service.repository.UserRepository;
import com.krzywdek19.user_service.service.EmailSenderService;
import com.krzywdek19.user_service.service.EmailVerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailSenderService emailSenderService;
    private final UserRepository userRepository;

    @Override
    public void createAndSendVerificationToken(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");

        var verificationToken = EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        tokenRepository.save(verificationToken);

        emailSenderService.sendEmail(
                user.getEmail(),
                "Verify your email",
                "Your verification token: " + token
        );
    }

    @Override
    @Transactional
    public void verify(String token) {
        var verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidVerificationTokenException("Invalid Verification Token"));

        if(verificationToken.getExpiresAt().isBefore(Instant.now())){
            throw new InvalidVerificationTokenException("Token has expired");
        }
        var user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }
}
