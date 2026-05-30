package com.inventory_service.Services;


import com.inventory_service.DTOs.InventoryDTO.InventoryRequest;
import com.inventory_service.DTOs.InventoryDTO.InventoryResponse;
import com.inventory_service.DTOs.InventoryDTO.InventoryWithProduct;
import com.inventory_service.Entities.Inventory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryService {
    InventoryResponse create(InventoryRequest request);

    InventoryResponse fetchById(Long id);

    InventoryResponse fetchByProductId(Long productId);

    List<InventoryResponse> fetchAll();
    List<InventoryResponse> fetchAllByProductId(List<Long> productIds);

    void deleteById(Long id);
    void deleteByProductId(Long productId);

    // fetch a inventory with a product
    InventoryWithProduct getInventoryDetails(Long inventoryId);
    // fetches all the inventory with product in details
    List<InventoryWithProduct> getAll();

    long getAvailableStockQuantity(Long productId);


    Inventory findById(Long id);

    Inventory findByIdWithLock(Long id);
}
