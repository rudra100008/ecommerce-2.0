package com.inventory_service.DTOs.ReservationDTO;

import com.inventory_service.Enums.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long reservationId,
        Long inventoryId,
        Long userId,
        Long productId,
        Long reservedQuantity,
        LocalDateTime reservedAt,
        LocalDateTime expiresAt,
        ReservationStatus status
) {
}
