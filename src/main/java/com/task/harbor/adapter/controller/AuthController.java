package com.task.harbor.adapter.controller;

import com.task.harbor.application.dto.auth.*;
import com.task.harbor.application.dto.response.ApiResponseService;
import com.task.harbor.application.usecase.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseService.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request: missing required fields, invalid email format, or password does not meet requirements (min 8 chars, must start with letter, alphanumeric only)")
    @ApiResponse(responseCode = "400", description = "Email already exists")
    public Mono<ResponseEntity<ApiResponseService<RegisterResponse>>> register(@Valid @RequestBody RegisterRequest request) {
        return registerUseCase.execute(request)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseService.success(user)));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return tokens")
    @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = ApiResponseService.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request: missing required fields, invalid email format, or password does not meet requirements")
    @ApiResponse(responseCode = "400", description = "Invalid credentials: email not found or password incorrect")
    @ApiResponse(responseCode = "400", description = "Invalid TOTP code")
    public Mono<ResponseEntity<ApiResponseService<TokenResponse>>> login(@Valid @RequestBody LoginRequest request) {
        return loginUseCase.execute(request)
                .map(token -> ResponseEntity.ok(ApiResponseService.success(token)));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate user session")
    @ApiResponse(responseCode = "200", description = "Logout successful")
    public Mono<ResponseEntity<ApiResponseService<Object>>> logout(@RequestHeader("X-User-Id") String userId) {
        return logoutUseCase.execute(UUID.fromString(userId))
                .thenReturn(ResponseEntity.ok(ApiResponseService.success(null)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refresh expired access token using refresh token")
    @ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseService.class)))
    @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    public Mono<ResponseEntity<ApiResponseService<TokenResponse>>> refresh(@RequestBody RefreshRequest request) {
        return refreshTokenUseCase.execute(request.refreshToken())
                .map(token -> ResponseEntity.ok(ApiResponseService.success(token)));
    }

    @PostMapping("/2fa/setup")
    @Operation(summary = "Setup 2FA", description = "Generate TOTP secret for two-factor authentication")
    @ApiResponse(
            responseCode = "200",
            description = "2FA secret generated",
            content = @Content(schema = @Schema(implementation = ApiResponseService.class)))
    public Mono<ResponseEntity<ApiResponseService<TotpSetupResponse>>> setup2fa(
            @RequestHeader("X-User-Id") String userId) {
        return totpUseCase.setup(UUID.fromString(userId))
                .map(response -> ResponseEntity.ok(ApiResponseService.success(response)));
    }

    @PostMapping("/2fa/verify")
    @Operation(summary = "Verify 2FA code", description = "Verify TOTP code for two-factor authentication")
    @ApiResponse(
            responseCode = "200",
            description = "Code verified",
            content = @Content(schema = @Schema(implementation = ApiResponseService.class)))
    @ApiResponse(responseCode = "400", description = "Invalid or missing TOTP code")
    @ApiResponse(responseCode = "400", description = "User not found or 2FA not setup")
    public Mono<ResponseEntity<ApiResponseService<Boolean>>> verify2fa(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody TotpVerifyRequest request) {
        return totpUseCase.verify(UUID.fromString(userId), request.code())
                .map(result -> ResponseEntity.ok(ApiResponseService.success(result)));
    }
}