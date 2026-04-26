package com.product.product_service.DTOs;

public record ProductRequest(
        String productName,
        String description,
        Long price
) {
}
