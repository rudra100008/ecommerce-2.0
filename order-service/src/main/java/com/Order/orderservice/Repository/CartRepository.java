package com.Order.orderservice.Repository;

import com.Order.orderservice.Entities.Cart;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    boolean existsByUserId(Long userId);

    @EntityGraph(attributePaths = {"cartItems"})
    Optional<Cart> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT DISTINCT c FROM Cart c
            LEFT JOIN FETCH c.cartItems
            WHERE c.userId = :userId
            """)
    Optional<Cart> findByUserIdWithLock(@Param("userId") Long userId);
}
