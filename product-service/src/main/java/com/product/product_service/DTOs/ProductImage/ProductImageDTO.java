package com.product.product_service.DTOs.ProductImage;

import java.util.List;

public record ProductImageDTO(
        Long id,
        String imageUrl,
        String publicId,
        boolean primaryImage,
        String folder,
        List<Long> productIds
) {
}
