package com.inventory_service.Controllers;

import com.inventory_service.DTOs.Error.ValidationErrorResponse;
import com.inventory_service.DTOs.Error.ValidationErrors;
import com.inventory_service.DTOs.ReservationDTO.ReservationRequest;
import com.inventory_service.DTOs.ReservationDTO.ReservationResponse;
import com.inventory_service.Entities.Reservation;
import com.inventory_service.Enums.ReservationStatus;
import com.inventory_service.Services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/inventory/reservation")
@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;



    @PostMapping("/create")
    public ResponseEntity<?> createReservation(
             @Valid  @RequestBody ReservationRequest request,
             BindingResult result
            ){
        if(result.hasErrors()){
            List<ValidationErrorResponse> errorResponses = new ArrayList<>();
            result.getFieldErrors()
                    .forEach(f ->
                    errorResponses.add(new ValidationErrorResponse(f.getField(),f.getDefaultMessage()))
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrors(errorResponses));
        }
        ReservationResponse response =  this.reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateReservationQuantity(
            @Valid @RequestBody ReservationRequest request,
            BindingResult result
    ){
        if(result.hasErrors()){
            List<ValidationErrorResponse> errorResponses = new ArrayList<>();
            result.getFieldErrors()
                    .forEach(f ->
                            errorResponses.add(new ValidationErrorResponse(f.getField(),f.getDefaultMessage()))
                    );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrors(errorResponses));
        }
        ReservationResponse response = this.reservationService.updateReservationQuantity(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/releaseReservation/user/{userId}")
    public ResponseEntity<?> releaseAllReservation(
            @PathVariable Long userId,
            @RequestParam("productIds") List<Long> productIds
    ){
        this.reservationService.releaseAllReservation(userId,productIds);
        return ResponseEntity.status(HttpStatus.OK).body("Reservation Deleted Successfully");
    }
    @DeleteMapping("/delete/user/{userId}/product/{productId}")
    public ResponseEntity<?> deleteReservation(
            @PathVariable Long userId,
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

    @GetMapping("/fetch/user/{userId}/product/{productId}")
    public ResponseEntity<?> fetchByUserIdAndProductId(
            @PathVariable Long userId,
            @PathVariable Long productId
    ){
        ReservationResponse response = this.reservationService.fetchActiveByUserIdAndProductId(userId,productId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/validateReservation/user/{userId}")
    public ResponseEntity<?> checkStatus(
            @PathVariable Long userId,
            @RequestParam("ids") List<Long> productIds
    ){
        List<ReservationResponse> responses = this.reservationService.validateReservation(userId,productIds);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @DeleteMapping("/deductStock/user/{userId}")
    public ResponseEntity<?> convertReservation(
            @PathVariable Long userId,
            @RequestParam("productIds")List<Long> productIds
    ){
        this.reservationService.convertReservation(userId,productIds);

        return ResponseEntity.status(HttpStatus.OK).body("Stock deducted from Inventory.");
    }

}
