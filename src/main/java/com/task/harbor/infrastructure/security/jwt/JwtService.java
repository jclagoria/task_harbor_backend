package com.task.harbor.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider tokenProvider;

    public String generateAccessToken(String email, String role, UUID tenantId) {
        return tokenProvider.generateAccessToken(email, role, tenantId);
    }

    public String generateRefreshToken(String email) {
        return tokenProvider.generateRefreshToken(email);
    }

    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    public Claims extractClaims(String token) {
        return tokenProvider.parseToken(token);
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public UUID extractTenantId(String token) {
        String tenantId = extractClaims(token).get("tenantId", String.class);
        return tenantId != null ? UUID.fromString(tenantId) : null;
    }
}