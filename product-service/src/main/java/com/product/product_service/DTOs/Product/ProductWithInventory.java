package com.product.product_service.DTOs.Product;

import com.product.product_service.DTOs.Inventory.InventoryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithInventory {
    private ProductResponse product;
    private InventoryDTO inventory;
}
