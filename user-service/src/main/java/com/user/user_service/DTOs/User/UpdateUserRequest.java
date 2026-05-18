package com.user.user_service.DTOs.User;

public record UpdateUserRequest(
        String username,
        String phoneNumber,
        String fullName
) {
}
