package com.task.harbor.application.usecase.auth;

import com.task.harbor.domain.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private SessionRepository sessionRepository;

    private LogoutUseCase logoutUseCase;

    @BeforeEach
    void setUp() {
        logoutUseCase = new LogoutUseCase(sessionRepository);
    }

    @Test
    void execute_Success() {
        UUID userId = UUID.randomUUID();
        
        when(sessionRepository.deleteByUserId(userId)).thenReturn(Mono.empty());

        StepVerifier.create(logoutUseCase.execute(userId))
                .verifyComplete();

        verify(sessionRepository).deleteByUserId(userId);
    }
}