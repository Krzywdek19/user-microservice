package com.krzywdek19.user_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "secret")
public record JwtProperties(
        String key,
        long accessDuration,
        long refreshDuration,
        String issuer
) {}