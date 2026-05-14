package com.task.harbor.application.usecase.auth;

import com.task.harbor.application.dto.auth.RegisterRequest;
import com.task.harbor.domain.entity.User;
import com.task.harbor.domain.repository.UserRepository;
import com.task.harbor.infrastructure.security.password.BCryptPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegisterUseCase {
    
    private final UserRepository userRepository;
    private final BCryptPasswordService passwordService;
    
    public Mono<User> execute(RegisterRequest request) {
        return userRepository.existsByEmail(request.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email already exists"));
                    }
                    
                    User user = User.builder()
                            .id(UUID.randomUUID())
                            .email(request.email())
                            .passwordHash(passwordService.hashPassword(request.password()))
                            .role("USER")
                            .tenantId(UUID.randomUUID())
                            .attributes(new java.util.HashMap<>())
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();
                    
                    return userRepository.save(user);
                });
    }
}