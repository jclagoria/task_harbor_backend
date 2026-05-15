package com.task.harbor.application.dto.auth;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    long expiresIn
) {}