package com.krzywdek19.api_gateway.security;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PublicPaths {

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/v1/auth",
            "/v3/api-docs",
            "/swagger-ui",
            "/actuator"
    );

    public boolean isPublic(String path) {
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }
}
