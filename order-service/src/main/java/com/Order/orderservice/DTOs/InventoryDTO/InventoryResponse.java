package com.Order.orderservice.DTOs.InventoryDTO;

public record InventoryResponse(
        Long inventoryId,
        Long productId,
        long stockQuantity
) {
}
