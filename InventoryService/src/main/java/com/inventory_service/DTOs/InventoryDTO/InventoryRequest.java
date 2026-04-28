package com.inventory_service.DTOs.InventoryDTO;

public record InventoryRequest(
        Long productId,
        long stockQuantity
) {
}
