package com.task.harbor.application.usecase.auth;

import com.task.harbor.application.dto.auth.LoginRequest;
import com.task.harbor.application.dto.auth.TokenResponse;
import com.task.harbor.domain.entity.Session;
import com.task.harbor.domain.entity.User;
import com.task.harbor.domain.repository.SessionRepository;
import com.task.harbor.domain.repository.UserRepository;
import com.task.harbor.infrastructure.security.jwt.JwtService;
import com.task.harbor.infrastructure.security.password.BCryptPasswordService;
import com.task.harbor.infrastructure.security.totp.TotpService;
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

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private BCryptPasswordService passwordService;

    @Mock
    private TotpService totpService;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(userRepository, sessionRepository, jwtService, passwordService, totpService);
        ReflectionTestUtils.setField(loginUseCase, "accessTokenExpiry", 900L);
    }

    @Test
    void execute_Success() {
        LoginRequest request = new LoginRequest("test@example.com", "password123", null);
        
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .role("USER")
                .tenantId(UUID.randomUUID())
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordService.verifyPassword(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateAccessToken(anyString(), anyString(), any())).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(passwordService.hashPassword(anyString())).thenReturn("refreshTokenHash");
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(loginUseCase.execute(request))
                .expectNextMatches(token -> 
                        token.accessToken().equals("accessToken") &&
                        token.refreshToken().equals("refreshToken")
                )
                .verifyComplete();
    }

    @Test
    void execute_InvalidCredentials() {
        LoginRequest request = new LoginRequest("test@example.com", "wrongpassword", null);
        
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordService.verifyPassword(anyString(), anyString())).thenReturn(false);

        StepVerifier.create(loginUseCase.execute(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void execute_UserNotFound() {
        LoginRequest request = new LoginRequest("notfound@example.com", "password123", null);
        
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(loginUseCase.execute(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void execute_WithTotp_ValidCode() {
        LoginRequest request = new LoginRequest("test@example.com", "password123", "123456");
        
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .totpSecret("SECRET123")
                .role("USER")
                .tenantId(UUID.randomUUID())
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordService.verifyPassword(anyString(), anyString())).thenReturn(true);
        when(totpService.verifyCode(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateAccessToken(anyString(), anyString(), any())).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(passwordService.hashPassword(anyString())).thenReturn("refreshTokenHash");
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(loginUseCase.execute(request))
                .expectNextMatches(token -> 
                        token.accessToken().equals("accessToken")
                )
                .verifyComplete();
    }

    @Test
    void execute_WithTotp_InvalidCode() {
        LoginRequest request = new LoginRequest("test@example.com", "password123", "000000");
        
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .totpSecret("SECRET123")
                .role("USER")
                .tenantId(UUID.randomUUID())
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordService.verifyPassword(anyString(), anyString())).thenReturn(true);
        when(totpService.verifyCode(anyString(), anyString())).thenReturn(false);

        StepVerifier.create(loginUseCase.execute(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}