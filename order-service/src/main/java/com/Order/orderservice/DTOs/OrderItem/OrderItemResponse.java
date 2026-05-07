package com.Order.orderservice.DTOs.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        Integer quantity,
        BigDecimal priceAtPurchase,
        BigDecimal discountAtPurchase,
        BigDecimal subTotal
) {
}
