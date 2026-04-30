package com.product.product_service.DTOs.ProductImage;

public record ProductImageDTO(
        Long id,
        String imageUrl,
        boolean primaryImage
) {
}
