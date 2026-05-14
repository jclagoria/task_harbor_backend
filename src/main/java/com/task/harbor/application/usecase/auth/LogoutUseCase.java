package com.task.harbor.application.usecase.auth;

import com.task.harbor.domain.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LogoutUseCase {
    
    private final SessionRepository sessionRepository;
    
    public Mono<Void> execute(UUID userId) {
        return sessionRepository.deleteByUserId(userId);
    }
}