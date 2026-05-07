package com.inventoryservice.Validators;

import com.inventoryservice.Enums.ReservationStatus;
import com.inventoryservice.Repository.InventoryRepository;
import com.inventoryservice.Repository.ReservationRepository;
import com.shared_library.Exceptions.BusinessInvalidException;
import com.shared_library.Exceptions.InsufficientStockException;
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
            throw new InsufficientStockException(String.format(
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
            throw new BusinessInvalidException(String.format("Active reservation for product %d already exists.",productId));
        }
    }

}
