package com.inventory_service.DTOs.InventoryDTO;


import com.inventory_service.DTOs.ProductResponse;


public record InventoryWithProduct (
        Long inventoryId,
        Long stockQuantity,
        ProductResponse product
) {
}
