package com.Order.orderservice.fallback;

import com.Order.orderservice.DTOs.ReservationDTO.ReservationRequest;
import com.Order.orderservice.DTOs.ReservationDTO.ReservationResponse;
import com.Order.orderservice.client.InventoryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class InventoryClientFallback implements InventoryClient {
    @Override
    public ReservationResponse createReservation(ReservationRequest request){
        throw new RuntimeException(
                "Inventory service is currently unavailable.Please try again."
        );
    }

    @Override
    public ReservationResponse updateReservationQuantity(ReservationRequest request) {
        log.error("Fallback: updateReservationQuantity failed for userId: {}, productId: {}",
                request.userId(), request.productId());
        throw new RuntimeException("Inventory service is currently unavailable. Cannot update reservation.");
    }

    @Override
    public void deleteReservation(Long userId, Long productId) {
        log.warn("Fallback: deleteReservation called for userId: {}, productId: {}", userId, productId);
    }

    @Override
    public List<ReservationResponse> validateActiveReservation(Long userId, List<Long> productIds) {
        log.warn("Fallback: validateActiveReservation - Returning empty list for userId: {}, productIds: {}",
                userId, productIds);
        // Return empty list - assuming no active reservations
        return Collections.emptyList();
    }

    @Override
    public void convertReservations(Long userId, List<Long> productIds) {
        log.error("Fallback: convertReservations FAILED for userId: {}, productIds: {}", userId, productIds);
        // Must throw exception to prevent order confirmation without inventory deduction
        throw new RuntimeException("Inventory service is unavailable. Cannot confirm order. Please try again.");
    }

    @Override
    public void releaseAllReservation(Long userId, List<Long> productIds) {
        log.warn("Fallback: releaseAllReservation called for userId: {}, productIds: {}", userId, productIds);
    }
}
