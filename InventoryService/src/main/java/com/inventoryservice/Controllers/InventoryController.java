package com.inventoryservice.Controllers;


import com.inventoryservice.DTOs.InventoryDTO.InventoryRequest;
import com.inventoryservice.DTOs.InventoryDTO.InventoryResponse;
import com.inventoryservice.DTOs.InventoryDTO.InventoryWithProduct;
import com.inventoryservice.Services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/inventory")
@RestController
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping()
    public ResponseEntity<InventoryResponse> create(
            @RequestBody InventoryRequest request
    ){
        InventoryResponse response = this.inventoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    public ResponseEntity<?> fetchByProductId(
            @PathVariable("productId")Long productId
    ){
        InventoryResponse response = this.inventoryService.fetchByProductId(productId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
