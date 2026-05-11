package com.user.user_service.DTOs.AuthDTO;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String role,
        Long userId
) {
}
