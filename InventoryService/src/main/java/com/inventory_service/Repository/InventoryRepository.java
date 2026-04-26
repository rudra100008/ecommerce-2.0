package com.inventory_service.Repository;

import com.inventory_service.Entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    Optional<Inventory> findByProductId(@Param("productId")Long productId);

//    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
//    void deleteByProductId(@Param("productId")Long productId);
}
