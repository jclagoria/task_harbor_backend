package com.task.harbor.adapter.controller;

import com.task.harbor.application.dto.auth.*;
import com.task.harbor.application.dto.response.ApiResponse;
import com.task.harbor.application.usecase.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "User authentication and session management")
public class AuthController {
    
    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final TotpUseCase totpUseCase;
    
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Mono<ResponseEntity<ApiResponse<Object>>> register(@Valid @RequestBody RegisterRequest request) {
        return registerUseCase.execute(request)
                .map(user -> ResponseEntity.ok(ApiResponse.success(
                        Map.of("userId", user.getId(), "email", user.getEmail())
                )));
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return tokens")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Mono<ResponseEntity<ApiResponse<TokenResponse>>> login(@Valid @RequestBody LoginRequest request) {
        return loginUseCase.execute(request)
                .map(token -> ResponseEntity.ok(ApiResponse.success(token)));
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate user session")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout successful")
    public Mono<ResponseEntity<ApiResponse<Object>>> logout(@RequestHeader("X-User-Id") String userId) {
        return logoutUseCase.execute(UUID.fromString(userId))
                .thenReturn(ResponseEntity.ok(ApiResponse.success(null)));
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refresh expired access token using refresh token")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Mono<ResponseEntity<ApiResponse<TokenResponse>>> refresh(@RequestBody RefreshRequest request) {
        return refreshTokenUseCase.execute(request.refreshToken())
                .map(token -> ResponseEntity.ok(ApiResponse.success(token)));
    }
    
    @PostMapping("/2fa/setup")
    @Operation(summary = "Setup 2FA", description = "Generate TOTP secret for two-factor authentication")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "2FA secret generated", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Mono<ResponseEntity<ApiResponse<TotpSetupResponse>>> setup2fa(@RequestHeader("X-User-Id") String userId) {
        return totpUseCase.setup(UUID.fromString(userId))
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)));
    }
    
    @PostMapping("/2fa/verify")
    @Operation(summary = "Verify 2FA code", description = "Verify TOTP code for two-factor authentication")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Code verified", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Mono<ResponseEntity<ApiResponse<Boolean>>> verify2fa(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody TotpVerifyRequest request) {
        return totpUseCase.verify(UUID.fromString(userId), request.code())
                .map(result -> ResponseEntity.ok(ApiResponse.success(result)));
    }
}