package com.task.harbor.application.usecase.auth;

import com.task.harbor.application.dto.auth.TokenResponse;
import com.task.harbor.domain.entity.Session;
import com.task.harbor.domain.entity.User;
import com.task.harbor.domain.repository.SessionRepository;
import com.task.harbor.domain.repository.UserRepository;
import com.task.harbor.infrastructure.security.jwt.JwtService;
import com.task.harbor.infrastructure.security.password.BCryptPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class RefreshTokenUseCase {
    
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordService passwordService;
    
    @Value("${app.jwt.access-token-expiry}")
    private long accessTokenExpiry;
    
    public Mono<TokenResponse> execute(String refreshToken) {
        String tokenHash = passwordService.hashPassword(refreshToken);
        
        return sessionRepository.findByRefreshTokenHash(tokenHash)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid refresh token")))
                .flatMap(session -> {
                    if (session.getExpiresAt().isBefore(Instant.now())) {
                        return Mono.error(new IllegalArgumentException("Refresh token expired"));
                    }
                    
                    return userRepository.findById(session.getUserId())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                            .flatMap(user -> {
                                String newAccessToken = jwtService.generateAccessToken(
                                        user.getEmail(),
                                        user.getRole(),
                                        user.getTenantId()
                                );
                                String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());
                                String newRefreshTokenHash = passwordService.hashPassword(newRefreshToken);
                                
                                session.setRefreshTokenHash(newRefreshTokenHash);
                                session.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
                                
                                return sessionRepository.save(session)
                                        .thenReturn(new TokenResponse(newAccessToken, newRefreshToken, accessTokenExpiry));
                            });
                });
    }
}