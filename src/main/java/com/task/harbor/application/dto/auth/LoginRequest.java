package com.task.harbor.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(max = 72, message = "Password cannot exceed 72 bytes")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9]*$", message = "Password must start with a letter and contain only alphanumeric characters")
    String password,
    
    String totpCode
) {}