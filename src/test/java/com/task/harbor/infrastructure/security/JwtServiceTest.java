package com.task.harbor.infrastructure.security;

import com.task.harbor.infrastructure.security.jwt.JwtService;
import com.task.harbor.infrastructure.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(tokenProvider);
    }

    @Test
    void generateAccessToken_DelegateToProvider() {
        String email = "test@example.com";
        String role = "USER";
        UUID tenantId = UUID.randomUUID();

        when(tokenProvider.generateAccessToken(email, role, tenantId)).thenReturn("accessToken");

        String result = jwtService.generateAccessToken(email, role, tenantId);

        assertEquals("accessToken", result);
    }

    @Test
    void generateRefreshToken_DelegateToProvider() {
        String email = "test@example.com";

        when(tokenProvider.generateRefreshToken(email)).thenReturn("refreshToken");

        String result = jwtService.generateRefreshToken(email);

        assertEquals("refreshToken", result);
    }

    @Test
    void validateToken_DelegateToProvider() {
        String token = "validToken";

        when(tokenProvider.validateToken(token)).thenReturn(true);

        boolean result = jwtService.validateToken(token);

        assertTrue(result);
    }

    @Test
    void extractClaims_DelegateToProvider() {
        String token = "someToken";
        Claims claims = org.mockito.Mockito.mock(Claims.class);

        when(tokenProvider.parseToken(token)).thenReturn(claims);

        Claims result = jwtService.extractClaims(token);

        assertEquals(claims, result);
    }

    @Test
    void extractEmail_ReturnSubject() {
        String token = "someToken";
        Claims claims = org.mockito.Mockito.mock(Claims.class);

        when(tokenProvider.parseToken(token)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("test@example.com");

        String result = jwtService.extractEmail(token);

        assertEquals("test@example.com", result);
    }

    @Test
    void extractRole_ReturnRoleClaim() {
        String token = "someToken";
        Claims claims = org.mockito.Mockito.mock(Claims.class);

        when(tokenProvider.parseToken(token)).thenReturn(claims);
        when(claims.get("role", String.class)).thenReturn("ADMIN");

        String result = jwtService.extractRole(token);

        assertEquals("ADMIN", result);
    }

    @Test
    void extractTenantId_ReturnParsedUuid() {
        String token = "someToken";
        UUID tenantId = UUID.randomUUID();
        Claims claims = org.mockito.Mockito.mock(Claims.class);

        when(tokenProvider.parseToken(token)).thenReturn(claims);
        when(claims.get("tenantId", String.class)).thenReturn(tenantId.toString());

        UUID result = jwtService.extractTenantId(token);

        assertEquals(tenantId, result);
    }

    @Test
    void extractTenantId_ReturnNullWhenNotPresent() {
        String token = "someToken";
        Claims claims = org.mockito.Mockito.mock(Claims.class);

        when(tokenProvider.parseToken(token)).thenReturn(claims);
        when(claims.get("tenantId", String.class)).thenReturn(null);

        UUID result = jwtService.extractTenantId(token);

        assertNull(result);
    }
}