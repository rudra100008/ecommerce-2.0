package com.product.product_service.DTOs;

public record InventoryDTO(
        Long inventoryId,
        Long productId,
        long stockQuantity
) {
}
