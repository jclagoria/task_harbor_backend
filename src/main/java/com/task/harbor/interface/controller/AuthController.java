package com.task.harbor.interface.controller;

import com.task.harbor.application.dto.auth.*;
import com.task.harbor.application.dto.response.ApiResponse;
import com.task.harbor.application.usecase.auth.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final TotpUseCase totpUseCase;
    
    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponse<Object>>> register(@Valid @RequestBody RegisterRequest request) {
        return registerUseCase.execute(request)
                .map(user -> ResponseEntity.ok(ApiResponse.success(
                        Map.of("userId", user.getId(), "email", user.getEmail())
                )));
    }
    
    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse<TokenResponse>>> login(@Valid @RequestBody LoginRequest request) {
        return loginUseCase.execute(request)
                .map(token -> ResponseEntity.ok(ApiResponse.success(token)));
    }
    
    @PostMapping("/logout")
    public Mono<ResponseEntity<ApiResponse<Object>>> logout(@RequestHeader("X-User-Id") String userId) {
        return logoutUseCase.execute(UUID.fromString(userId))
                .thenReturn(ResponseEntity.ok(ApiResponse.success(null)));
    }
    
    @PostMapping("/refresh")
    public Mono<ResponseEntity<ApiResponse<TokenResponse>>> refresh(@RequestBody RefreshRequest request) {
        return refreshTokenUseCase.execute(request.getRefreshToken())
                .map(token -> ResponseEntity.ok(ApiResponse.success(token)));
    }
    
    @PostMapping("/2fa/setup")
    public Mono<ResponseEntity<ApiResponse<TotpSetupResponse>>> setup2fa(@RequestHeader("X-User-Id") String userId) {
        return totpUseCase.setup(UUID.fromString(userId))
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)));
    }
    
    @PostMapping("/2fa/verify")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> verify2fa(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody TotpVerifyRequest request) {
        return totpUseCase.verify(UUID.fromString(userId), request.getCode())
                .map(result -> ResponseEntity.ok(ApiResponse.success(result)));
    }
}