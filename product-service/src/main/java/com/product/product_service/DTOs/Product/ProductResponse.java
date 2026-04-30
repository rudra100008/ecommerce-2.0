package com.product.product_service.DTOs.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal discount,
        String sku,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
