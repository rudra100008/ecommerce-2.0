package com.inventory_service.Controllers;


import com.inventory_service.DTOs.InventoryDTO.InventoryRequest;
import com.inventory_service.DTOs.InventoryDTO.InventoryResponse;
import com.inventory_service.DTOs.InventoryDTO.InventoryWithProduct;
import com.inventory_service.Services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/inventory")
@RestController
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping()
    public InventoryResponse create(
            @RequestBody InventoryRequest request
    ){
        return this.inventoryService.create(request);
    }

    @GetMapping
    public List<InventoryResponse> fetchAllInventory(){
        return this.inventoryService.fetchAll();
    }

    @GetMapping("/{id}")
    public InventoryResponse fetchById(
            @PathVariable("id")Long id
    ){
        return this.inventoryService.fetchById(id);
    }

    @GetMapping("/product/{productId}")
    public InventoryResponse fetchByProductId(
            @PathVariable("productId")Long productId
    ){
        return  this.inventoryService.fetchByProductId(productId);
    }

    @DeleteMapping("/{id}")
    public String deleteById(
            @PathVariable("id")Long id
    ){
         this.inventoryService.deleteById(id);
         return "Inventory deleted Successfully";
    }

    @DeleteMapping("/product/{productId}")
    public String deleteByProductId(
            @PathVariable("productId")Long productId
    ){
        this.inventoryService.deleteByProductId(productId);
        return "Inventory deleted Successfully";
    }


    @GetMapping("/inventory_product/{id}")
    public InventoryWithProduct fetchInventoryWithProduct(
            @PathVariable Long id
    ){
        return this.inventoryService.getInventoryDetails(id);
    }

    @GetMapping("/inventories_products")
    public List<InventoryWithProduct> fetchInventoriesWithProducts(){
        return  this.inventoryService.getAll();
    }
}
