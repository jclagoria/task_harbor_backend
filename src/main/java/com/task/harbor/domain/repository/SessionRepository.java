package com.task.harbor.domain.repository;

import com.task.harbor.domain.entity.Session;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface SessionRepository extends R2dbcRepository<Session, UUID> {
    Mono<Session> findByRefreshTokenHash(String refreshTokenHash);
    Mono<Void> deleteByUserId(UUID userId);
    Mono<Void> deleteByExpiresAtBefore(Instant now);
}