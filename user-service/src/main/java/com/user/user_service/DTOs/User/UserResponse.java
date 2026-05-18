package com.user.user_service.DTOs.User;

import com.user.user_service.DTOs.Address.AddressResponse;
import com.user.user_service.Enums.RoleStatus;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long userId,
        String username,
        String email,
        String phoneNumber,
        String fullName,
        String imageUrl,
        Boolean imageCustomized,
        Boolean active,
        RoleStatus role,
        List<AddressResponse> addresses,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
