package com.Order.order_service.DTOs.InventoryDTO;

public record InventoryRequest(
        Long productId,
        long stockQuantity
) {
}
