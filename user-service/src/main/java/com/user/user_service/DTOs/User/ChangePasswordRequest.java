package com.user.user_service.DTOs.User;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
