package com.task.harbor.application.dto.auth;

public record TotpVerifyRequest(
    String code
) {}