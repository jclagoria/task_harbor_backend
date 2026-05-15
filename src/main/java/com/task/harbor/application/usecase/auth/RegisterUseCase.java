package com.task.harbor.application.usecase.auth;

import com.task.harbor.application.dto.auth.RegisterRequest;
import com.task.harbor.application.dto.auth.RegisterResponse;
import com.task.harbor.domain.repository.UserRepository;
import com.task.harbor.infrastructure.security.password.BCryptPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterUseCase {
    
    private final UserRepository userRepository;
    private final BCryptPasswordService passwordService;
    
    public Mono<RegisterResponse> execute(RegisterRequest request) {
        log.info("Registering user: {}", request.email());
        return userRepository.existsByEmail(request.email())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email already exists"));
                    }
                    
                    UUID tenantId = UUID.randomUUID();
                    Instant now = Instant.now();
                    String passwordHash = passwordService.hashPassword(request.password());
                    
                    return userRepository.insertAndReturn(request.email(), passwordHash, "USER", tenantId, now, now)
                            .map(user -> new RegisterResponse(user.getId().toString(), user.getEmail()))
                            .doOnNext(response -> log.info("User registered successfully: {}", response.userId()));
                });
    }
}