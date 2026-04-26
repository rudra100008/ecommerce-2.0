package com.product.product_service.DTOs;

public record ProductResponse(
        Long productId,
        String productName,
        String description,
        Long price
) {
}
