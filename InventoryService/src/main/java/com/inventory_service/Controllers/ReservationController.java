package com.inventory_service.Controllers;

import com.inventory_service.DTOs.ReservationDTO.ReservationRequest;
import com.inventory_service.DTOs.ReservationDTO.ReservationResponse;
import com.inventory_service.Services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/inventory/reservation")
@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;



    @PostMapping("/create")
    public ResponseEntity<?> createReservation(
            @RequestHeader("X-User-Id") Long userId,
             @Valid  @RequestBody ReservationRequest request
    ){
        ReservationResponse response =  this.reservationService.createReservation(request,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateReservationQuantity(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ReservationRequest request
    ){
        ReservationResponse response = this.reservationService.updateReservationQuantity(request,userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/releaseReservation")
    public ResponseEntity<?> releaseAllReservation(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("productIds") List<Long> productIds
    ){
        this.reservationService.releaseAllReservation(userId,productIds);
        return ResponseEntity.status(HttpStatus.OK).body("Reservation Deleted Successfully");
    }
    @DeleteMapping("/delete/product/{productId}")
    public ResponseEntity<?> deleteReservation(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId
    ){
        this.reservationService.deleteReservation(userId,productId);
        return ResponseEntity.status(HttpStatus.OK).body("Reservation Deleted Successfully");
    }

    @GetMapping("/get_reserved_quantity/product/{productId}")
    public ResponseEntity<?> getTotalReservationByProductId(
            @PathVariable Long productId
    ){
        Long totalReservation = this.reservationService.getTotalReservationByProductId(productId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "productId",productId,
                "totalReservation",totalReservation
        ));
    }

    @GetMapping("/fetch/product/{productId}")
    public ResponseEntity<?> fetchByUserIdAndProductId(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long productId
    ){
        ReservationResponse response = this.reservationService.fetchActiveByUserIdAndProductId(userId,productId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/validateReservation")
    public ResponseEntity<?> checkStatus(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("ids") List<Long> productIds
    ){
        List<ReservationResponse> responses = this.reservationService.validateReservation(userId,productIds);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping("/deductStock")
    public ResponseEntity<?> convertReservation(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("productIds")List<Long> productIds
    ){
        this.reservationService.convertReservation(userId,productIds);

        return ResponseEntity.status(HttpStatus.OK).body("Stock deducted from Inventory.");
    }

}
