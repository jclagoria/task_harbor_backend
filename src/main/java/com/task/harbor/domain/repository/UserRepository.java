package com.task.harbor.domain.repository;

import com.task.harbor.domain.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface UserRepository extends R2dbcRepository<User, UUID> {
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    
    @Query("INSERT INTO users (email, password_hash, role, tenant_id, created_at, updated_at) " +
            "VALUES (:email, :passwordHash, :role, :tenantId, :createdAt, :updatedAt) " +
            "RETURNING id, email, password_hash, role, tenant_id, created_at, updated_at")
    Mono<User> insertAndReturn(String email, String passwordHash, String role, UUID tenantId, Instant createdAt, Instant updatedAt);
}