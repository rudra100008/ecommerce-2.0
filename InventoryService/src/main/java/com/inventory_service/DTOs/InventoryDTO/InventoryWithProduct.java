package com.inventory_service.DTOs.InventoryDTO;


import com.inventory_service.DTOs.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryWithProduct {
    private Long inventoryId;
    private long stockQuantity;
    private ProductDTO productDTO;
}
