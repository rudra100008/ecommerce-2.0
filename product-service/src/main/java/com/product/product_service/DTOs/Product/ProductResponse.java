package com.product.product_service.DTOs.Product;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Double price,
        Double discount
) {
}
