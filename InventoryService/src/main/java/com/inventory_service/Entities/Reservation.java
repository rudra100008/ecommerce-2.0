package com.inventory_service.Entities;

import com.inventory_service.Enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "reservations",uniqueConstraints = @UniqueConstraint(columnNames = {"inventory_id","status"}))
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {
        "inventory"
})
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id",nullable = false)
    private Inventory inventory;

    @Column(nullable = false)
    private Long reservedQuantity;

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;



    public boolean isActive() {
        return status == ReservationStatus.ACTIVE &&
                LocalDateTime.now().isBefore(expiresAt);
    }

    public void releaseReservedQuantity(Integer quantity){
        this.reservedQuantity -= quantity;
        if(reservedQuantity < 0){
            reservedQuantity = 0L;
        }
    }

    // Helper method to check if expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
