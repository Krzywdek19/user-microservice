package com.krzywdek19.user_service.service.impl;

import com.krzywdek19.user_service.exception.UserNotFoundException;
import com.krzywdek19.user_service.model.UserStatus;
import com.krzywdek19.user_service.repository.UserRepository;
import com.krzywdek19.user_service.service.JwtService;
import com.krzywdek19.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final JwtBlackListServiceImpl jwtBlackListService;
    private final JwtService jwtService;


    @Transactional
    public void deleteUserByEmail(String email, String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        if (token != null) {
            String jti = jwtService.extractJti(token);
            Date exp = jwtService.extractExpiration(token);
            Duration ttl = Duration.ofMillis(Math.max(exp.getTime() - System.currentTimeMillis(), 0));
            jwtBlackListService.blacklist(jti, ttl);
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            user.setStatus(UserStatus.DELETED);
            userRepository.save(user);
        });
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null) return null;
        if (!authorizationHeader.startsWith("Bearer ")) return null;
        return authorizationHeader.substring(7);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User with this username not found"));
    }
}
