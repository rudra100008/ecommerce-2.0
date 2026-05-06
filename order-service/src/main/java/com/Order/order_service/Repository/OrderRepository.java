package com.Order.order_service.Repository;

import com.Order.order_service.Entities.Order;
import com.Order.order_service.Enum.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    boolean existsByUserIdAndStatus(Long userId , OrderStatus status);

    @EntityGraph(attributePaths = {"orderItems"})
    Optional<Order> findByIdAndUserIdAndStatus(Long id,Long userId, OrderStatus status);

    @Query("SELECT o from Order o WHERE o.status = 'DELIVERED'")
    List<Order> findWithDeliveredStatus();

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.orderItems
            WHERE o.id = :id
                AND o.userId = :userId
            """)
    Optional<Order> findDraftByIdAndUserId(@Param("id")Long id, @Param("userId")Long userId);
    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.orderItems
            WHERE o.id = :id
                AND o.userId = :userId
                AND o.status = 'DRAFT'
            """)
    Optional<Order> findByIdAndUserIdWithDraftStatus(@Param("id")Long id, @Param("userId")Long userId);

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.orderItems
            WHERE o.id = :id
                AND o.userId = :userId
                AND o.status = 'CONFIRMED'
            """)
    Optional<Order> findByIdAndUserIdWithConfirmedStatus(@Param("id")Long id, @Param("userId")Long userId);



}
