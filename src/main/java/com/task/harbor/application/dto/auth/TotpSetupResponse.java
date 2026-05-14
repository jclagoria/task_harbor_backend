package com.task.harbor.application.dto.auth;

public record TotpSetupResponse(
    String secret,
    String qrCode
) {}