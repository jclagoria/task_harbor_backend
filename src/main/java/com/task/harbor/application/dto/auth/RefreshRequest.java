package com.task.harbor.application.dto.auth;

public record RefreshRequest(
    String refreshToken
) {}