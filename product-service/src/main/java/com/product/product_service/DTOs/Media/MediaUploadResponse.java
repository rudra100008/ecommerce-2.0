package com.product.product_service.DTOs.Media;

public record MediaUploadResponse(
        String imageUrl,
        String publicId,
        String folder,
        long size,
        String format
) {
}
