package com.inventoryservice.Services;


import com.inventoryservice.DTOs.InventoryDTO.InventoryRequest;
import com.inventoryservice.DTOs.InventoryDTO.InventoryResponse;
import com.inventoryservice.DTOs.InventoryDTO.InventoryWithProduct;
import com.inventoryservice.Entities.Inventory;
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

    long getAvailableStockQuantity(Long productId);


    Inventory findById(Long id);

    Inventory findByIdWithLock(Long id);
}
