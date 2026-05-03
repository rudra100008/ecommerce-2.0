package com.product.product_service.DTOs.ProductImage;

public record ProductImageRequest(
        String imageUrl,
        String publicId,
        boolean primaryImage
) {
}
