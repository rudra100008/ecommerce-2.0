package com.Order.order_service.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "order_items")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal priceAtPurchase;

    @Column(precision = 10,scale = 2)
    private BigDecimal discountAtPurchase;



    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal subTotal;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    // ======= HELPER METHOD =======

    public void calculateAndSetSubTotal() {
        BigDecimal price = priceAtPurchase != null ? priceAtPurchase : BigDecimal.ZERO;
        BigDecimal discount = discountAtPurchase != null ? discountAtPurchase : BigDecimal.ZERO;
        BigDecimal qty = BigDecimal.valueOf(quantity != null ? quantity : 0);

        this.subTotal = price.subtract(discount)
                .multiply(qty)
                .setScale(2, RoundingMode.HALF_UP);
    }


}
