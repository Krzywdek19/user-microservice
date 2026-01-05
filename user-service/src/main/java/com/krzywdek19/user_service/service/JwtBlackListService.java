package com.krzywdek19.user_service.service;

import java.time.Duration;

public interface JwtBlackListService {
    void blacklist(String jti, Duration ttl);
    boolean isBlacklisted(String jti);
}
