package com.inventoryservice.DTOs.InventoryDTO;

public record InventoryRequest(
        Long productId,
        long stockQuantity
) {
}
