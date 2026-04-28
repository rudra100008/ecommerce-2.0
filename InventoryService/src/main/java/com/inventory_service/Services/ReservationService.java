package com.inventory_service.Services;

import com.inventory_service.DTOs.ReservationDTO.ReservationRequest;
import com.inventory_service.DTOs.ReservationDTO.ReservationResponse;
import org.springframework.stereotype.Service;

@Service
public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request);
    ReservationResponse updateReservationQuantity(ReservationRequest request);
    void deleteReservation(Long userId, Long inventoryId);
    long getTotalReservationByInventoryId(Long inventoryId);

}
