package com.inventoryservice.Helpers;

import com.inventoryservice.Component.ReservationProperties;
import com.inventoryservice.DTOs.ReservationDTO.ReservationRequest;
import com.inventoryservice.Entities.Inventory;
import com.inventoryservice.Entities.Reservation;
import com.inventoryservice.Enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationHelper {
    private final ReservationProperties properties;

    public Reservation buildReservation(ReservationRequest request, Inventory inventory){
        LocalDateTime now = LocalDateTime.now();
        return Reservation.builder()
                .inventory(inventory)
                .userId(request.userId())
                .reservedQuantity(request.reservedQuantity())
                .reservedAt(now)
                .expiresAt(now.plusMinutes(properties.expiryMinutes()))
                .status(ReservationStatus.ACTIVE)
                .build();
    }
}
