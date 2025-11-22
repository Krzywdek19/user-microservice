package com.krzywdek19.user_service.security.jwt;

import com.krzywdek19.user_service.config.JwtProperties;
import com.krzywdek19.user_service.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    private final JwtProperties jwtProperties;
    private final JwtParser jwtParser;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(getSigningKey()).build();
    }

    @Override
    public String generateAccessToken(UserDetails user) {
        return buildToken(user, jwtProperties.accessDuration());
    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        return buildToken(user, jwtProperties.refreshDuration());
    }

    private String buildToken(UserDetails user, long duration) {
        Date now = Date.from(Instant.now());
        Date expiration = Date.from(Instant.now().plusMillis(duration));
        return Jwts.builder()
                .setIssuer(jwtProperties.issuer())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setSubject(user.getUsername())
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = jwtProperties.key().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        final String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token) && isIssuerValid(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private boolean isIssuerValid(String token) {
        String issuer = extractClaim(token, Claims::getIssuer);
        return issuer != null && issuer.equals(jwtProperties.issuer());
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final var claims = jwtParser.parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }
}
