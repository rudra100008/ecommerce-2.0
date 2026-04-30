package com.product.product_service.Services;

import com.product.product_service.Constants.ApiConstants;
import com.product.product_service.DTOs.Inventory.InventoryDTO;
import com.product.product_service.DTOs.Inventory.InventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping(ApiConstants.API_INVENTORY)
    InventoryDTO createInventory(@RequestBody InventoryRequest request);


    @GetMapping(ApiConstants.API_INVENTORY_BY_PRODUCT)
    InventoryDTO fetchInventoryByProductId(@PathVariable Long productId);

    @DeleteMapping(ApiConstants.API_INVENTORY + "/product/{productId}")
    void deleteByProductId(@PathVariable Long productId);
}
