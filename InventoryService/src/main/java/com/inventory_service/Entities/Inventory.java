package com.inventory_service.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "inventory")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {
        "reservations"
})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private long stockQuantity;

    @Column(nullable = false)
    private Long productId;

    @OneToMany(mappedBy = "inventory",fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();



}
