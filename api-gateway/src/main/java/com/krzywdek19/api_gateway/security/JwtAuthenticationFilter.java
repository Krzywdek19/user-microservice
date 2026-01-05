package com.krzywdek19.api_gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j

public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final PublicPaths publicPaths;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    private String key(String jti) {
        return "blacklist:jwt:" + jti;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        var request = exchange.getRequest();
        var path = request.getURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod().name())) return chain.filter(exchange);
        if (publicPaths.isPublic(path)) return chain.filter(exchange);

        var authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        var token = authHeader.substring(7);

        try {
            var claims = jwtUtil.parseToken(token);

            String jti = claims.getId();
            if (jti != null && redisTemplate.hasKey(key(jti))) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            var mutatedRequest = request.mutate()
                    .header("X-User-Email", claims.getSubject())
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
