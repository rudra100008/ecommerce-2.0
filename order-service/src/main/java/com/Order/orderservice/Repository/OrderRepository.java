package com.Order.orderservice.Repository;

import com.Order.orderservice.Entities.Order;
import com.Order.orderservice.Enum.OrderStatus;
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
    Optional<Order> findByIdAndUserId(@Param("id")Long id, @Param("userId")Long userId);
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
                AND o.status = 'CONFIRMED'
            """)
    Optional<Order> findByIdAndUserIdWithConfirmedStatus(@Param("id")Long id);

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.orderItems
            WHERE o.id = :id
                AND o.status = 'SHIPPED'
            """)
    Optional<Order> findByIdAndUserIdWithShippedStatus(@Param("id")Long id);



}
