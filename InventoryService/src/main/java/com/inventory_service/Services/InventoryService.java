package com.inventory_service.Services;


import com.inventory_service.DTOs.InventoryRequest;
import com.inventory_service.DTOs.InventoryResponse;
import com.inventory_service.DTOs.InventoryWithProduct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryService {

    InventoryResponse create(InventoryRequest request);

    InventoryResponse fetchById(Long id);

    InventoryResponse fetchByProductId(Long productId);

    List<InventoryResponse> fetchAll();

    void deleteById(Long id);
    void deleteByProductId(Long productId);

    // fetch a inventory with a product
    InventoryWithProduct getInventoryDetails(Long inventoryId);
    // fetches all the inventory with product in details
    List<InventoryWithProduct> getAll();
}
