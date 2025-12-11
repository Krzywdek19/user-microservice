package com.krzywdek19.user_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LoginRateLimiter {
    private final StringRedisTemplate redisTemplate;

    private static final int MAX_ATTEMPTS = 10;
    private static final Duration BLOCK_TIME = Duration.ofMinutes(5);

    private String key(String email) {
        return "login_attempts:" + email;
    }

    public boolean isBlocked(String email) {
        String value = redisTemplate.opsForValue().get(key(email));
        if (value != null) {
            int attempts = Integer.parseInt(value);
            return attempts >= MAX_ATTEMPTS;
        }
        return false;
    }

    public void recordFailedAttempt(String email) {
        String redisKey = key(email);
        Long attempts = redisTemplate.opsForValue().increment(redisKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(redisKey, BLOCK_TIME);
        }
    }

    public void resetAttempts(String email) {
        redisTemplate.delete(key(email));
    }
}
