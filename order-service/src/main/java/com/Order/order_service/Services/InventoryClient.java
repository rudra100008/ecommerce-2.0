package com.Order.order_service.Services;

import com.Order.order_service.DTOs.InventoryDTO.InventoryResponse;
import com.Order.order_service.DTOs.ReservationDTO.ReservationRequest;
import com.Order.order_service.DTOs.ReservationDTO.ReservationResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/inventory/product/{productId}")
    InventoryResponse fetchByProductId(@PathVariable Long productId);


    @PostMapping("/api/inventory/reservation")
    ReservationResponse createReservation(@Valid @RequestBody ReservationRequest request);
}
