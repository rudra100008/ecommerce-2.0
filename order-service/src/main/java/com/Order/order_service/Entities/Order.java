package com.Order.order_service.Entities;

import com.Order.order_service.Enum.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private Long userId; // user who ordered

    private LocalDateTime estimatedDeliveryDate; // when order will be delivered

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status; //

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal totalAmount; // this amount is total of all the orderItem



    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @OneToMany(mappedBy = "order",orphanRemoval = true,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Embedded
    private ShippingAddress shippingAddress;

    @CreationTimestamp
    private LocalDateTime createdAt;  // when order was placed or created

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // ======= HELPER METHOD ==========

    public void addOrderItem(OrderItem item){
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item){
        orderItems.remove(item);
        item.setOrder(null);
    }

    public void calculateTotalAmount(){
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add); // this result = result.add(item.getSubTotal)
    }

    public boolean isCancellable(){
        return status == OrderStatus.DRAFT || status == OrderStatus.CONFIRMED;
    }

    public  boolean isConfirmed(){
        return  status == OrderStatus.CONFIRMED;
    }


}
