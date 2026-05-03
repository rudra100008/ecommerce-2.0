package com.Order.order_service.DTOs.ShippingAddress;

import com.Order.order_service.Enum.AddressType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// ✅ Add validation
public record ShippingAddressDTO(
        @NotBlank(message = "Province is required")
        String province,

        @NotBlank(message = "District is required")
        String district,

        @NotBlank(message = "Municipality is required")
        String municipality,

        @NotNull(message = "Ward number is required")
        @Min(value = 1, message = "Ward number must be at least 1")
        @Max(value = 35, message = "Ward number cannot exceed 35")
        Integer wardNumber,

        String landmark,
        String area,
        String houseNumber,

        @NotNull(message = "Address type is required")
        AddressType addressType
) {}
