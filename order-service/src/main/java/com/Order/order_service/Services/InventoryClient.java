package com.Order.order_service.Services;

import com.Order.order_service.DTOs.InventoryDTO.InventoryResponse;
import com.Order.order_service.DTOs.ReservationDTO.ReservationRequest;
import com.Order.order_service.DTOs.ReservationDTO.ReservationResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/inventory/product/{productId}")
    InventoryResponse fetchByProductId(@PathVariable Long productId);


    @PostMapping("/api/inventory/reservation/create")
    ReservationResponse createReservation(@Valid @RequestBody ReservationRequest request);

    @PutMapping("/api/inventory/reservation/update")
    ReservationResponse updateReservationQuantity(@Valid @RequestBody ReservationRequest request);

    @GetMapping("/api/inventory/reservation/get_reserved_quantity/product/{productId}")
    long getTotalReservationByProductId(@PathVariable Long productId);

    @DeleteMapping("/api/inventory/reservation/user/{userId}/product/{productId}")
    void deleteReservation(
            @PathVariable Long userId,
            @PathVariable Long productId
    );
}
