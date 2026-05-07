package com.Order.orderservice.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_item")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private Integer quantity;


    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false,precision = 10, scale = 2)
    private BigDecimal priceAtAddTime;

    @Column(precision = 10,scale = 2)
    private BigDecimal discountAtAddTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id",nullable = false)
    private Cart cart;
}
