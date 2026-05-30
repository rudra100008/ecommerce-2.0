package com.product.product_service.DTOs.Product;

import com.product.product_service.DTOs.Category.CategoryDTO;
import com.product.product_service.DTOs.Inventory.InventoryDTO;
import com.product.product_service.DTOs.ProductImage.ProductImageDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductAdminResponse(
        Long productId,
        String name,
        String description,
        BigDecimal price,
        BigDecimal discount,
        String sku,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        CategoryDTO category,
        InventoryDTO inventory,
        ProductImageDTO image
) {
}
