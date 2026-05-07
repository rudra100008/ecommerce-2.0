package com.Order.orderservice.DTOs.CartItem;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Integer quantity,
        Long productId,
        Long reservationId,
        BigDecimal priceAtAddTime,
        BigDecimal discountAtAddTime
) {
}
