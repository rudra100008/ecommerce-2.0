package com.Order.order_service.DTOs.ReservationDTO;

import com.Order.order_service.Enum.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long reservationId,
        Long inventoryId,
        Long userId,
        Long reservedQuantity,
        LocalDateTime reservedAt,
        LocalDateTime expiresAt,
        ReservationStatus status
) {
}
