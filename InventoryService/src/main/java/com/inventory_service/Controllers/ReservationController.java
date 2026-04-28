package com.inventory_service.Controllers;

import com.inventory_service.DTOs.ReservationDTO.ReservationRequest;
import com.inventory_service.DTOs.ReservationDTO.ReservationResponse;
import com.inventory_service.Services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/inventory/reservation")
@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;



    @PostMapping()
    public ReservationResponse createReservation(
             @Valid  @RequestBody ReservationRequest request
            ){

        return this.reservationService.createReservation(request);
    }

    @PutMapping
    public ReservationResponse updateReservationQuantity(
            @RequestBody ReservationRequest request
    ){
        return  this.reservationService.updateReservationQuantity(request);
    }

    @DeleteMapping("/user/{userId}/inventory/{inventoryId}")
    public String deleteReservation(
            @PathVariable Long userId,
            @PathVariable Long inventoryId
    ){
        this.reservationService.deleteReservation(userId,inventoryId);
        return "Reservation Deleted Successfully";
    }

    @GetMapping("get_reserved_quantity/inventory/{inventoryId}")
    public long getTotalReservationByInventoryId(
            @PathVariable Long inventoryId
    ){
        return  this.reservationService.getTotalReservationByInventoryId(inventoryId);
    }

}
