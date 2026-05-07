package com.inventoryservice.DTOs.InventoryDTO;


import com.inventoryservice.DTOs.ProductResponse;


public record InventoryWithProduct (
        Long inventoryId,
        Long stockQuantity,
        ProductResponse product
) {
}
