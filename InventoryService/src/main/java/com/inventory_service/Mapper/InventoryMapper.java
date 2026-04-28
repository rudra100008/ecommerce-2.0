package com.inventory_service.Mapper;


import com.inventory_service.DTOs.InventoryDTO.InventoryRequest;
import com.inventory_service.DTOs.InventoryDTO.InventoryResponse;
import com.inventory_service.DTOs.InventoryDTO.InventoryWithProduct;
import com.inventory_service.DTOs.ProductDTO;
import com.inventory_service.Entities.Inventory;
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
    @Mapping(source = "productDTO",target = "productDTO")
    InventoryWithProduct toInventoryWithProduct( Inventory inventory, ProductDTO productDTO);


    default List<InventoryWithProduct> toInventoriesWithProducts(List<Inventory> inventories,List<ProductDTO> productDTOS){
        if(inventories == null && productDTOS == null){
            return  new ArrayList<>();
        }
        Map<Long,ProductDTO> productDTOMap = productDTOS.stream()
                .collect(Collectors.toMap(p-> p.getId(),p->p));

        return inventories != null ? inventories.stream()
                .map(inv -> toInventoryWithProduct(inv, productDTOMap.get(inv.getProductId())))
                .toList() : null;
    }
}
