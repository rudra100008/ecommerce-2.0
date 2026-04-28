package com.product.product_service.DTOs.Inventory;

public record InventoryDTO(
        Long inventoryId,
        Long productId,
        long stockQuantity
) {
}
