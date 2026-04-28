package com.inventory_service.Validators;

import com.inventory_service.Enums.ReservationStatus;
import com.inventory_service.Repository.ReservationRepository;
import com.inventory_service.Services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationValidators {
    private final ReservationRepository reservationRepository;
    private final InventoryService inventoryService;


    public void validateStockAvailability(long inventoryId, long requestedQuantity){
        long available = inventoryService.getAvailableStockQuantity(inventoryId);
        if(available < requestedQuantity){
            throw new IllegalStateException(String.format(
                    "Insufficient stock. Requested: %d,Available: %d",
                    requestedQuantity,available
            ));
        }
    }

    // validate in createReservation to make sure same product in inventory is not reserved multiple times rather quantity is increased
    public void validateNoDuplicateReservation(Long userId, Long inventoryId) {
        boolean exists = reservationRepository
                .existsByUserIdAndInventoryIdAndStatus(
                        userId, inventoryId, ReservationStatus.ACTIVE);
        if (exists) {
            throw new IllegalStateException("Active reservation already exists");
        }
    }

}
