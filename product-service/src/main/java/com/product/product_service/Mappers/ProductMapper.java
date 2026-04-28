package com.product.product_service.Mappers;

import com.product.product_service.DTOs.Inventory.InventoryDTO;
import com.product.product_service.DTOs.Product.ProductRequest;
import com.product.product_service.DTOs.Product.ProductResponse;
import com.product.product_service.DTOs.Product.ProductWithInventory;
import com.product.product_service.Entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id",ignore = true)
    Product toProduct(ProductRequest request);

    ProductResponse toProductResponse(Product product);

    @Mapping(source = "product",target = "product")
    @Mapping(source = "inventory", target = "inventory")
    ProductWithInventory toProductWithInventory(ProductResponse product, InventoryDTO inventory);
}
