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
    
    @Query("INSERT INTO users (first_name, last_name, email, password_hash, role, tenant_id, created_at, updated_at) " +
            "VALUES (:firstName, :lastName, :email, :passwordHash, :role, :tenantId, :createdAt, :updatedAt) " +
            "RETURNING id, first_name, last_name, email, password_hash, role, tenant_id, created_at, updated_at")
    Mono<User> insertAndReturn(String firstName, String lastName, String email, String passwordHash, String role, UUID tenantId, Instant createdAt, Instant updatedAt);
}