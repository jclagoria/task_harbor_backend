package com.task.harbor.application.usecase.auth;

import com.task.harbor.application.dto.auth.RegisterRequest;
import com.task.harbor.domain.repository.UserRepository;
import com.task.harbor.infrastructure.security.password.BCryptPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordService passwordService;

    private RegisterUseCase registerUseCase;

    @BeforeEach
    void setUp() {
        registerUseCase = new RegisterUseCase(userRepository, passwordService);
    }

    @Test
    void execute_EmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("existing@example.com", "password123", "John", "Doe");
        
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(registerUseCase.execute(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}