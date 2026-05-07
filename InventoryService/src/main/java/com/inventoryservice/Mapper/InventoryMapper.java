package com.inventoryservice.Mapper;


import com.inventoryservice.DTOs.InventoryDTO.InventoryRequest;
import com.inventoryservice.DTOs.InventoryDTO.InventoryResponse;
import com.inventoryservice.DTOs.InventoryDTO.InventoryWithProduct;
import com.inventoryservice.DTOs.ProductResponse;
import com.inventoryservice.Entities.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    @Mapping(target = "id",ignore = true)
    Inventory toInventory(InventoryRequest request);

    @Mapping(source = "id",target = "inventoryId")
    InventoryResponse toInventoryResponse(Inventory inventory);


    default List<InventoryResponse> toInventoryResponses(List<Inventory> inventories){
        if(inventories == null){
            return  new ArrayList<>();
        }

        return inventories.stream()
                .map(this::toInventoryResponse)
                .toList();
    }

    @Mapping(source = "inventory.id",target = "inventoryId")
    @Mapping(source = "inventory.stockQuantity", target = "stockQuantity")
    @Mapping(source = "product",target = "product")
    InventoryWithProduct toInventoryWithProduct(Inventory inventory, ProductResponse product);


    default List<InventoryWithProduct> toInventoriesWithProducts(List<Inventory> inventories,List<ProductResponse> products){
        if(inventories == null && products == null){
            return  new ArrayList<>();
        }
        Map<Long,ProductResponse> productDTOMap = products.stream()
                .collect(Collectors.toMap(ProductResponse::id, p->p));

        return inventories != null ? inventories.stream()
                .map(inv -> toInventoryWithProduct(inv, productDTOMap.get(inv.getProductId())))
                .toList() : null;
    }
}
