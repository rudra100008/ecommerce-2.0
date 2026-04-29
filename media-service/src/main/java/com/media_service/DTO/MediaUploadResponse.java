package com.media_service.DTO;

public record MediaUploadResponse(
        String imageUrl,
        String publicId,
        String folder,
        long size,
        String format
) {
}
