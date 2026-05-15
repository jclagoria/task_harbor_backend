package com.task.harbor.application.usecase.auth;

import com.task.harbor.application.dto.auth.TotpSetupResponse;
import com.task.harbor.domain.entity.User;
import com.task.harbor.domain.repository.UserRepository;
import com.task.harbor.infrastructure.security.totp.TotpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TotpUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TotpService totpService;

    private TotpUseCase totpUseCase;

    @BeforeEach
    void setUp() {
        totpUseCase = new TotpUseCase(userRepository, totpService);
    }

    @Test
    void setup_Success() {
        UUID userId = UUID.randomUUID();
        
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(totpService.generateSecret()).thenReturn("SECRET123");
        when(totpService.generateQrCodeUrl(anyString(), anyString())).thenReturn("otpauth://totp/Test:test@example.com?secret=SECRET123&issuer=TaskHarbor");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(totpUseCase.setup(userId))
                .expectNextMatches(response -> 
                        response.secret().equals("SECRET123") &&
                        response.qrCode().contains("SECRET123")
                )
                .verifyComplete();
    }

    @Test
    void setup_UserNotFound() {
        UUID userId = UUID.randomUUID();
        
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(totpUseCase.setup(userId))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void verify_Success() {
        UUID userId = UUID.randomUUID();
        
        User user = User.builder()
                .id(userId)
                .totpSecret("SECRET123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(totpService.verifyCode("SECRET123", "123456")).thenReturn(true);

        StepVerifier.create(totpUseCase.verify(userId, "123456"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void verify_UserNotFound() {
        UUID userId = UUID.randomUUID();
        
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(totpUseCase.verify(userId, "123456"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void verify_InvalidCode() {
        UUID userId = UUID.randomUUID();
        
        User user = User.builder()
                .id(userId)
                .totpSecret("SECRET123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(totpService.verifyCode("SECRET123", "000000")).thenReturn(false);

        StepVerifier.create(totpUseCase.verify(userId, "000000"))
                .expectNext(false)
                .verifyComplete();
    }
}