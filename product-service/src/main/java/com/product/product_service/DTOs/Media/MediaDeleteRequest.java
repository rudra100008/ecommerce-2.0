package com.product.product_service.DTOs.Media;

import jakarta.validation.constraints.NotNull;

public record MediaDeleteRequest (
        @NotNull(message = "publicId is required")
        String publicId
){
}
