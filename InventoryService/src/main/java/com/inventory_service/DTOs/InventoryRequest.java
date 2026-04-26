package com.inventory_service.DTOs;

public record InventoryRequest(
        Long productId,
        long stockQuantity
) {
}
