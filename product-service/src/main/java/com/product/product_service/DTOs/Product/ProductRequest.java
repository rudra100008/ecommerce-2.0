package com.product.product_service.DTOs.Product;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        String description,
        BigDecimal price,
        BigDecimal discount,
        String sku

) {
}
