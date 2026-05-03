package com.product.product_service.DTOs.Product;

import java.math.BigDecimal;

public record UpdateProductRequest(

        String name,
        String description,

        BigDecimal price,

        BigDecimal discount
) {
}
