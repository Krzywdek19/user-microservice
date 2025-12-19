package com.krzywdek19.user_service.service.impl;

import com.krzywdek19.user_service.exception.UserNotFoundException;
import com.krzywdek19.user_service.model.UserStatus;
import com.krzywdek19.user_service.repository.UserRepository;
import com.krzywdek19.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;

    @Transactional
    public void deleteUserByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setStatus(UserStatus.DELETED);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User with this username not found"));
    }
}
