package com.inventory_service.DTOs.ReservationDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationRequest (
        @NotNull(message = "Product Id is required")
        Long productId,
        @NotNull(message = "User Id is required.")
        Long userId,
        @Positive(message = "reserved quantity must be positive.")
        @Min(value = 1, message = "Quantity must be greater than 0.")
        @Max(value = 100, message = "Quantity cannot exceed 100.")
        Long reservedQuantity
){
}
