package com.Order.order_service.DTOs.InventoryDTO;

import com.Order.order_service.DTOs.Product.ProductResponse;
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
    private ProductResponse product;
}
