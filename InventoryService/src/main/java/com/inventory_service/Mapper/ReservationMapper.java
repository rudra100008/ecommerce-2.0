package com.inventory_service.Mapper;

import com.inventory_service.DTOs.ReservationDTO.ReservationRequest;
import com.inventory_service.DTOs.ReservationDTO.ReservationResponse;
import com.inventory_service.Entities.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "inventory",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "reservedAt",ignore = true)
    @Mapping(target = "expiresAt",ignore = true)
    @Mapping(target = "status",ignore = true)
    Reservation toReservation(ReservationRequest request);


    @Mapping(source = "id",target = "reservationId")
    @Mapping(source = "inventory.id",target = "inventoryId")
    ReservationResponse toResponse(Reservation reservation);
}
