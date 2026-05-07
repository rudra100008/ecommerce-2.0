package com.inventoryservice.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        Long inventoryId,
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
