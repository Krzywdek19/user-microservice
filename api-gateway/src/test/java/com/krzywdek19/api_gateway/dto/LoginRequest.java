package com.krzywdek19.api_gateway.dto;

public record LoginRequest(
        String email,
        String password
) {
}
