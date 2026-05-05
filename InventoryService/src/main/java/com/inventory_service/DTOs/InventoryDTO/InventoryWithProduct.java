package com.inventory_service.DTOs.InventoryDTO;


import com.inventory_service.DTOs.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public record InventoryWithProduct (
        Long inventoryId,
        Long stockQuantity,
        ProductResponse product
) {
}
