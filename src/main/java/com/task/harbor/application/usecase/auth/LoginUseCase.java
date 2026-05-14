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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoginUseCase {
    
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final JwtService jwtService;
    private final BCryptPasswordService passwordService;
    private final TotpService totpService;
    
    @Value("${app.jwt.access-token-expiry}")
    private long accessTokenExpiry;
    
    public Mono<TokenResponse> execute(LoginRequest request) {
        return userRepository.findByEmail(request.email())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid credentials")))
                .flatMap(user -> {
                    if (!passwordService.verifyPassword(request.password(), user.getPasswordHash())) {
                        return Mono.error(new IllegalArgumentException("Invalid credentials"));
                    }
                    
                    if (user.getTotpSecret() != null && !user.getTotpSecret().isEmpty()) {
                        if (request.totpCode() == null || !totpService.verifyCode(user.getTotpSecret(), request.totpCode())) {
                            return Mono.error(new IllegalArgumentException("Invalid TOTP code"));
                        }
                    }
                    
                    String accessToken = jwtService.generateAccessToken(
                            user.getEmail(), 
                            user.getRole(), 
                            user.getTenantId()
                    );
                    String refreshToken = jwtService.generateRefreshToken(user.getEmail());
                    
                    String refreshTokenHash = passwordService.hashPassword(refreshToken);
                    
                    Session session = Session.builder()
                            .id(UUID.randomUUID())
                            .userId(user.getId())
                            .refreshTokenHash(refreshTokenHash)
                            .createdAt(Instant.now())
                            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                            .build();
                    
                    return sessionRepository.save(session)
                            .thenReturn(new TokenResponse(accessToken, refreshToken, accessTokenExpiry));
                });
    }
}