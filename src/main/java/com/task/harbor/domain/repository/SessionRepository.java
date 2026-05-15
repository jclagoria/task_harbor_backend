package com.task.harbor.domain.repository;

import com.task.harbor.domain.entity.Session;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface SessionRepository extends R2dbcRepository<Session, UUID> {
    Mono<Session> findByRefreshTokenHash(String refreshTokenHash);
    Mono<Void> deleteByUserId(UUID userId);
    Mono<Void> deleteByExpiresAtBefore(Instant now);
    
    @Query("INSERT INTO sessions (user_id, refresh_token_hash, created_at, expires_at) VALUES (:userId, :refreshTokenHash, :createdAt, :expiresAt)")
    Mono<Long> insert(UUID userId, String refreshTokenHash, Instant createdAt, Instant expiresAt);
}