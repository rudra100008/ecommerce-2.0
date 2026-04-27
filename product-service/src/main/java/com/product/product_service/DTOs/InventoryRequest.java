package com.product.product_service.DTOs;

public record InventoryRequest(
        Long productId,
        long stockQuantity
) {
}
