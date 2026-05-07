package com.inventoryservice.DTOs.InventoryDTO;

public record InventoryResponse(
        Long inventoryId,
        Long productId,
        long stockQuantity
) {
}
