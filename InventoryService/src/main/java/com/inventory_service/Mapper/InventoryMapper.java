package com.inventory_service.Mapper;


import com.inventory_service.DTOs.InventoryRequest;
import com.inventory_service.DTOs.InventoryResponse;
import com.inventory_service.Entities.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

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
}
