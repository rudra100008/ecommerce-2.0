package com.inventory_service.Helpers;

import com.inventory_service.Component.ReservationProperties;
import com.inventory_service.DTOs.ReservationDTO.ReservationRequest;
import com.inventory_service.Entities.Inventory;
import com.inventory_service.Entities.Reservation;
import com.inventory_service.Enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationHelper {
    private final ReservationProperties properties;

    public Reservation buildReservation(ReservationRequest request, Inventory inventory,Long userId){
        LocalDateTime now = LocalDateTime.now();
        return Reservation.builder()
                .inventory(inventory)
                .userId(userId)
                .reservedQuantity(request.reservedQuantity())
                .reservedAt(now)
                .expiresAt(now.plusMinutes(properties.expiryMinutes()))
                .status(ReservationStatus.ACTIVE)
                .build();
    }
}
