package com.inventory_service.Helpers;

import com.inventory_service.DTOs.ReservationDTO.ReservationRequest;
import com.inventory_service.Entities.Inventory;
import com.inventory_service.Entities.Reservation;
import com.inventory_service.Enums.ReservationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReservationHelper {
    public Reservation buildReservation(ReservationRequest request, Inventory inventory){
        return Reservation.builder()
                .inventory(inventory)
                .userId(request.userId())
                .reservedQuantity(request.reservedQuantity())
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusWeeks(1))
                .status(ReservationStatus.ACTIVE)
                .build();
    }
}
