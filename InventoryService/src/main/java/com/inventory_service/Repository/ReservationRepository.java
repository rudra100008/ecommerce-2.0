package com.inventory_service.Repository;

import com.inventory_service.Entities.Reservation;
import com.inventory_service.Enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    boolean existsByUserIdAndInventoryIdAndStatus(long userId, long inventoryId, ReservationStatus status);
    Optional<Reservation> findByUserIdAndInventoryIdAndStatus(long userId,long inventoryId, ReservationStatus status);

    @Query("""
            SELECT COALESCE(SUM(reservedQuantity),0)
            FROM Reservation r
            WHERE r.inventory.id = :inventoryId
                  AND r.status = 'ACTIVE'
                  AND r.expiresAt > :now
            """)
    Long getTotalReservedQuantityByInventoryId(@Param("inventoryId") long inventoryId, @Param("now")LocalDateTime now);


    @Modifying
    @Query("""
    UPDATE Reservation r
    SET r.status = 'EXPIRED'
    WHERE r.status = 'ACTIVE'
    AND r.expiresAt < :now
    """)
    int expiryReservation(@Param("now") LocalDateTime now);
}
