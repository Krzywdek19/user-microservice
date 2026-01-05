package com.krzywdek19.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlackListServiceImpl {

    private final StringRedisTemplate redisTemplate;

    private String key(String jti) {
        return "blacklist:jwt:" + jti;
    }

    public void blacklist(String jti, Duration ttl) {
        if (jti == null || jti.isBlank()) return;
        if (ttl == null || ttl.isZero() || ttl.isNegative()) return;

        redisTemplate.opsForValue().set(key(jti), "1", ttl);
    }

    public boolean isBlacklisted(String jti) {
        return redisTemplate.hasKey(key(jti));
    }
}