package com.product.product_service.Services;

import com.product.product_service.Constants.ApiConstants;
import com.product.product_service.DTOs.InventoryDTO;
import com.product.product_service.DTOs.InventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping(ApiConstants.API_INVENTORY)
    InventoryDTO createInventory(@RequestBody InventoryRequest request);


    @GetMapping(ApiConstants.API_INVENTORY_BY_PRODUCT)
    InventoryDTO fetchInventoryByProductId(@PathVariable Long productId);
}
