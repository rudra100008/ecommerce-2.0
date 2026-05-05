package com.inventory_service.Validators;

import com.inventory_service.Enums.ReservationStatus;
import com.inventory_service.Repository.InventoryRepository;
import com.inventory_service.Repository.ReservationRepository;
import com.inventory_service.Services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationValidators {
    private final ReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;


    public void validateStockAvailability(Long productId, Long requestedQuantity){
        long available = inventoryRepository.getAvailableQuantity(productId, LocalDateTime.now());
        if(available < requestedQuantity){
            throw new IllegalStateException(String.format(
                    "Insufficient stock. Requested: %d,Available: %d",
                    requestedQuantity,available
            ));
        }
    }

    // validate in createReservation to make sure same product in inventory is not reserved multiple times rather quantity is increased
    public void validateNoDuplicateReservation(Long userId, Long productId) {
        boolean exists = reservationRepository
                .existsByUserIdAndProductIdAndStatus(
                        userId, productId, ReservationStatus.ACTIVE);
        if (exists) {
            throw new IllegalStateException(String.format("Active reservation for product %d already exists.",productId));
        }
    }

}
