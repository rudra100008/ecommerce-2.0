package com.Order.order_service.DTOs.InventoryDTO;

public record InventoryResponse(
        Long inventoryId,
        Long productId,
        long stockQuantity
) {
}
