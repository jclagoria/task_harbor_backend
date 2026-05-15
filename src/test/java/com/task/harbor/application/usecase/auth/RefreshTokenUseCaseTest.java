package com.task.harbor.application.usecase.auth;

import com.task.harbor.application.dto.auth.TokenResponse;
import com.task.harbor.domain.entity.Session;
import com.task.harbor.domain.entity.User;
import com.task.harbor.domain.repository.SessionRepository;
import com.task.harbor.domain.repository.UserRepository;
import com.task.harbor.infrastructure.security.jwt.JwtService;
import com.task.harbor.infrastructure.security.password.BCryptPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private BCryptPasswordService passwordService;

    private RefreshTokenUseCase refreshTokenUseCase;

    @BeforeEach
    void setUp() {
        refreshTokenUseCase = new RefreshTokenUseCase(sessionRepository, userRepository, jwtService, passwordService);
        ReflectionTestUtils.setField(refreshTokenUseCase, "accessTokenExpiry", 900L);
    }

    @Test
    void execute_Success() {
        String refreshToken = "validRefreshToken";
        UUID userId = UUID.randomUUID();
        
        Session session = Session.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .refreshTokenHash("tokenHash")
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .role("USER")
                .tenantId(UUID.randomUUID())
                .build();

        AtomicInteger callCount = new AtomicInteger(0);
        when(passwordService.hashPassword(anyString())).thenAnswer(inv -> {
            if (callCount.getAndIncrement() == 0) {
                return "tokenHash";
            }
            return "newHash";
        });
        when(sessionRepository.findByRefreshTokenHash("tokenHash")).thenReturn(Mono.just(session));
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(jwtService.generateAccessToken(anyString(), anyString(), any())).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("newRefreshToken");
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(refreshTokenUseCase.execute(refreshToken))
                .expectNextMatches(token -> 
                        token.accessToken().equals("newAccessToken") &&
                        token.refreshToken().equals("newRefreshToken")
                )
                .verifyComplete();
    }

    @Test
    void execute_InvalidToken() {
        when(passwordService.hashPassword(anyString())).thenReturn("invalidHash");
        when(sessionRepository.findByRefreshTokenHash("invalidHash")).thenReturn(Mono.empty());

        StepVerifier.create(refreshTokenUseCase.execute("invalidToken"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void execute_ExpiredToken() {
        String refreshToken = "expiredToken";
        
        Session session = Session.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .refreshTokenHash("tokenHash")
                .createdAt(Instant.now().minus(10, ChronoUnit.DAYS))
                .expiresAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();

        when(passwordService.hashPassword(anyString())).thenReturn("tokenHash");
        when(sessionRepository.findByRefreshTokenHash("tokenHash")).thenReturn(Mono.just(session));

        StepVerifier.create(refreshTokenUseCase.execute(refreshToken))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}