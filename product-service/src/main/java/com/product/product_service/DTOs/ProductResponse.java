package com.product.product_service.DTOs;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Double price,
        Double discount
) {
}
