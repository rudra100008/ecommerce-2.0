package com.Order.orderservice.client;

import com.Order.orderservice.DTOs.ReservationDTO.ReservationRequest;
import com.Order.orderservice.DTOs.ReservationDTO.ReservationResponse;
import com.Order.orderservice.fallback.InventoryClientFallbackFactory;
import com.shared_library.Config.FeignConfig;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "inventory-service",
        configuration = FeignConfig.class,
        fallbackFactory = InventoryClientFallbackFactory.class
)
public interface InventoryClient {

    @PostMapping("/api/inventory/reservation/create")
    ReservationResponse createReservation(@Valid @RequestBody ReservationRequest request);

    @PutMapping("/api/inventory/reservation/update")
    ReservationResponse updateReservationQuantity(@Valid @RequestBody ReservationRequest request);

    @DeleteMapping("/api/inventory/reservation/delete/product/{productId}")
    void deleteReservation(@PathVariable Long productId);

    @GetMapping("/api/inventory/reservation/validateReservation")
    List<ReservationResponse> validateActiveReservation(
            @RequestParam("ids") List<Long> productIds
    );

    @PostMapping("/api/inventory/reservation/deductStock")
    void convertReservations(@RequestParam("productIds") List<Long> productIds);

    @PostMapping("/api/inventory/reservation/releaseReservation")
    void releaseAllReservation(@RequestParam("productIds") List<Long> productIds);
}
