package com.inventoryservice.DTOs.ReservationDTO;

import com.inventoryservice.Enums.ReservationStatus;

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
