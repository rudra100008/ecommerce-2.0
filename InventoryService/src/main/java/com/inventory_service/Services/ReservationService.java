package com.inventory_service.Services;

import com.inventory_service.DTOs.ReservationDTO.ReservationRequest;
import com.inventory_service.DTOs.ReservationDTO.ReservationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request);
    ReservationResponse updateReservationQuantity(ReservationRequest request);
    void deleteReservation(Long userId, Long productId);
    void releaseAllReservation(Long userId,List<Long> productIds);
    Long getTotalReservationByProductId(Long productId);
    ReservationResponse fetchActiveByUserIdAndProductId(Long userId, Long productId);

    List<ReservationResponse> validateReservation(Long userId, List<Long> productIds);

    //deducts the stock quantity in inventory
    void convertReservation(Long userId, List<Long> productIds);


}
