package com.krzywdek19.user_service.E2E;

import com.krzywdek19.user_service.model.User;
import com.krzywdek19.user_service.model.UserStatus;
import com.krzywdek19.user_service.repository.UserRepository;
import com.krzywdek19.user_service.service.EmailVerificationService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class TestEmailVerificationService implements EmailVerificationService {

    private final UserRepository userRepository;

    public TestEmailVerificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createAndSendVerificationToken(User user) {
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void verify(String token) {
    }
}