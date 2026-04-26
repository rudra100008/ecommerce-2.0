package com.inventory_service.DTOs;

public record InventoryResponse(
        Long inventoryId,
        Long productId,
        long stockQuantity
) {
}
