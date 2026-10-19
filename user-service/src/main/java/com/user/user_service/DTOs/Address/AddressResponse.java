package com.user.user_service.DTOs.Address;

public record AddressResponse(
        Long addressId,
        String district,
        String province,
        String municipality,
        Integer wardNumber,
        String landmark
) {
}
