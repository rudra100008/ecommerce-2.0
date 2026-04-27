package com.product.product_service.DTOs;

public record ProductRequest(
        String name,
        String description,
        Double price,
        Double discount
) {
}
