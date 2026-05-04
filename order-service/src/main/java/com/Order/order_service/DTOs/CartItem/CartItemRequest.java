package com.Order.order_service.DTOs.CartItem;

import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
        @NotNull(message = "Quantity is required")
        Integer quantity,
        @NotNull(message = "Product ID is required")
        Long productId
) {
}
