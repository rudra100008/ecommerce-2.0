package com.inventory_service.DTOs.InventoryDTO;

public record InventoryResponse(
        Long inventoryId,
        Long productId,
        long stockQuantity
) {
}
