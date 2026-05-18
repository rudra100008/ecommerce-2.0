package com.user.user_service.DTOs.Address;

public record AddressRequest(
        String district,
        String province,
        String municipality,
        Integer wardNumber,
        String landmark
) {
}
