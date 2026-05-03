package com.product.product_service.DTOs.Product;

import com.product.product_service.DTOs.ProductImage.ProductImageRequest;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotNull(message = "Product name is required.")
        @NotBlank(message = "Product name is required.")
        String name,
        @NotNull(message = "Product description is required.")
        @NotBlank(message = "Product description is required.")
        String description,
        @Positive(message = "price must be positive.")
        @Min(value = 10, message = "Price must be minimum 10.")
        BigDecimal price,
        @Positive(message = "discount must be greater than 0.")
        @Max(value = 5000, message = "At most 5000 discount.")
        BigDecimal discount,

        String sku,
        @NotNull(message = "Stock quantity is required")
        @Min(value = 1,message = "Stock must be at least 1")
        Long stockQuantity,

        List<ProductImageRequest> images

) {
}
