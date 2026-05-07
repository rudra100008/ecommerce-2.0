package com.Order.orderservice.DTOs.InventoryDTO;

public record InventoryRequest(
        Long productId,
        long stockQuantity
) {
}
