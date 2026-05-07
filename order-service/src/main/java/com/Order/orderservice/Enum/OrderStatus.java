package com.Order.orderservice.Enum;

public enum OrderStatus {
    DRAFT,       // created, items reserved
    CONFIRMED,   // payment confirmed
    PROCESSING,  // being packed/prepared
    SHIPPED,     // out for delivery
    DELIVERED,   // received by customer
    CANCELLED    // cancelled
}
