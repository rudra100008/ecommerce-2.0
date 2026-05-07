package com.Order.orderservice.DTOs.ReservationDTO;

import com.Order.orderservice.Enum.ReservationStatus;

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
