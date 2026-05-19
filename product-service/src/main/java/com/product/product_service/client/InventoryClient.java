package com.product.product_service.client;

import com.product.product_service.Constants.ApiConstants;
import com.product.product_service.DTOs.Inventory.InventoryDTO;
import com.product.product_service.DTOs.Inventory.InventoryRequest;
import com.product.product_service.fallback.InventoryClientFallbackFactory;
import com.shared_library.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "inventory-service",
        configuration = FeignConfig.class,
        fallbackFactory = InventoryClientFallbackFactory.class
)
public interface InventoryClient {

    @PostMapping(ApiConstants.API_INVENTORY)
    InventoryDTO createInventory(@RequestBody InventoryRequest request);


    @GetMapping(ApiConstants.API_INVENTORY_BY_PRODUCT)
    InventoryDTO fetchInventoryByProductId(@PathVariable Long productId);

    @DeleteMapping(ApiConstants.API_INVENTORY + "/product/{productId}")
    void deleteByProductId(@PathVariable Long productId);
}
