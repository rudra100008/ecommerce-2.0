package com.inventoryservice.Mapper;

import com.inventoryservice.DTOs.ReservationDTO.ReservationRequest;
import com.inventoryservice.DTOs.ReservationDTO.ReservationResponse;
import com.inventoryservice.Entities.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

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
    @Mapping(source = "inventory.productId",target = "productId")
    ReservationResponse toResponse(Reservation reservation);


    default List<ReservationResponse> toReservationList(List<Reservation> reservations){
        if(reservations == null || reservations.isEmpty()){
            return new ArrayList<>();
        }

        return reservations.stream()
                .map(this::toResponse)
                .toList();
    }
}
