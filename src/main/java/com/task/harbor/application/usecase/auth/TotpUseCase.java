package com.task.harbor.application.usecase.auth;

import com.task.harbor.application.dto.auth.TotpSetupResponse;
import com.task.harbor.domain.entity.User;
import com.task.harbor.domain.repository.UserRepository;
import com.task.harbor.infrastructure.security.totp.TotpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TotpUseCase {
    
    private final UserRepository userRepository;
    private final TotpService totpService;
    
    public Mono<TotpSetupResponse> setup(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    String secret = totpService.generateSecret();
                    String qrCode = totpService.generateQrCodeUrl(secret, user.getEmail());
                    
                    user.setTotpSecret(secret);
                    return userRepository.save(user)
                            .thenReturn(new TotpSetupResponse(secret, qrCode));
                });
    }
    
    public Mono<Boolean> verify(UUID userId, String code) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    boolean valid = totpService.verifyCode(user.getTotpSecret(), code);
                    if (!valid) {
                        return Mono.error(new IllegalArgumentException("Invalid TOTP code"));
                    }
                    return Mono.just(true);
                });
    }
}