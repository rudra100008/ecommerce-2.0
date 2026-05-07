package com.Order.orderservice.client;

import com.Order.orderservice.DTOs.ReservationDTO.ReservationRequest;
import com.Order.orderservice.DTOs.ReservationDTO.ReservationResponse;
import com.shared_library.Config.FeignConfig;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "inventory-service",configuration = FeignConfig.class)
public interface InventoryClient {

//    @GetMapping("/api/inventory/product/{productId}")
//    InventoryResponse fetchByProductId(@PathVariable Long productId);


    @PostMapping("/api/inventory/reservation/create")
    ReservationResponse createReservation(@Valid @RequestBody ReservationRequest request);

    @PutMapping("/api/inventory/reservation/update")
    ReservationResponse updateReservationQuantity(@Valid @RequestBody ReservationRequest request);

//    @GetMapping("/api/inventory/reservation/get_reserved_quantity/product/{productId}")
//    long getTotalReservationByProductId(@PathVariable Long productId);
//
//    @GetMapping("/api/inventory/reservation/fetch/user/{userId}/product/{productId}")
//    ReservationResponse findActiveReservation(@PathVariable Long userId,@PathVariable Long productId);


    @DeleteMapping("/api/inventory/reservation/delete/user/{userId}/product/{productId}")
    void deleteReservation(
            @PathVariable Long userId,
            @PathVariable Long productId
    );

    @GetMapping("/api/inventory/reservation/validateReservation/user/{userId}")
    List<ReservationResponse> validateActiveReservation(
            @PathVariable Long userId,
            @RequestParam("ids") List<Long> productIds
    );

    @PostMapping("/api/inventory/reservation/deductStock/user/{userId}")
    void convertReservations(
            @PathVariable Long userId,
            @RequestParam("productIds")List<Long> productIds
    );

    @PostMapping("/api/inventory/reservation/releaseReservation/user/{userId}")
    void releaseAllReservation(
            @PathVariable Long userId,
            @RequestParam("productIds")List<Long> productIds
    );
}
