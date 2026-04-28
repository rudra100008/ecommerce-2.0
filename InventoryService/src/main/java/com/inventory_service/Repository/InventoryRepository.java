package com.inventory_service.Repository;

import com.inventory_service.Entities.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    Optional<Inventory> findByProductId(@Param("productId")Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i FROM Inventory i WHERE i.id = :id
            """)
    Optional<Inventory> findByIdWithLock(@Param("id")Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("""
            SELECT i FROM Inventory i WHERE i.id = :id
            """)
    Optional<Inventory> findByIdForRead(@Param("id")Long id);
    @Query("""
            SELECT i.stockQuantity - COALESCE(SUM(r.reservedQuantity),0)
            FROM Inventory i
            LEFT JOIN Reservation r ON r.inventory.id = i.id
                              AND r.status = 'ACTIVE'
                              AND r.expiresAt > :now
            WHERE i.id = :inventoryId
            """)
    Long getAvailableQuantity(@Param("inventoryId") Long inventoryId, @Param("now")LocalDateTime now);
}
