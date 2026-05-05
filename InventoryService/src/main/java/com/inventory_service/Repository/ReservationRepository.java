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

    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Reservation r
            WHERE r.userId = :userId
            AND r.inventory.productId = :productId
            AND r.status = :status
            """)
    boolean existsByUserIdAndProductIdAndStatus(@Param("userId") Long userId,
                                                @Param("productId") Long productId,
                                                @Param("status") ReservationStatus status);


    @Query("""
            SELECT r FROM Reservation r
            WHERE r.userId = :userId
            AND r.inventory.productId = :productId
            AND r.status = :status
            
            """)
    Optional<Reservation> findByUserIdAndProductIdAndStatus(@Param("userId") Long userId,@Param("productId") Long productId,@Param("status") ReservationStatus status);

    @Query("""
            SELECT COALESCE(SUM(reservedQuantity),0)
            FROM Reservation r
            WHERE r.inventory.productId = :productId
                  AND r.status = 'ACTIVE'
                  AND r.expiresAt > :now
            """)
    Long getTotalReservedQuantityByProductId(@Param("productId") Long productId, @Param("now")LocalDateTime now);


    @Modifying
    @Query("""
    UPDATE Reservation r
    SET r.status = 'EXPIRED'
    WHERE r.status = 'ACTIVE'
    AND r.expiresAt < :now
    """)
    int expiryReservation(@Param("now") LocalDateTime now);
}
