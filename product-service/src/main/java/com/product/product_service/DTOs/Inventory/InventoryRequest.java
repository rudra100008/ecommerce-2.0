package com.product.product_service.DTOs.Inventory;

public record InventoryRequest(
        Long productId,
        Long stockQuantity
) {
}
