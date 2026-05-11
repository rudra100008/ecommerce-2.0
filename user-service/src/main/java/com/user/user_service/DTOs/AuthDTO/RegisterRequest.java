package com.user.user_service.DTOs.AuthDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Full name is required")
        String fullName,
        @NotBlank(message = "username is required")
        String username,
        @NotBlank(message = "Email is required")
        @Email(message = "Email is required")
        String email,
        @NotBlank(message = "password is required")
        @Size(min = 8,message = "Password should be at least 8 characters")
        String password
) {}