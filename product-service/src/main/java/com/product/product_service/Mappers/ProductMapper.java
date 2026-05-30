package com.product.product_service.Mappers;

import com.product.product_service.DTOs.Category.CategoryDTO;
import com.product.product_service.DTOs.Inventory.InventoryDTO;
import com.product.product_service.DTOs.Product.*;
import com.product.product_service.DTOs.ProductImage.ProductImageDTO;
import com.product.product_service.Entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "sku", ignore = true)
    Product toProduct(ProductRequest request);


    ProductResponse toProductResponse(Product product);

    default List<ProductResponse> toProductResponseList(List<Product> products){
        if (products == null){
            return new ArrayList<>();
        }
        return products.stream()
                .map(this::toProductResponse)
                .toList();
    }

    default ProductDTO toProductDTO(
            Product product,
            InventoryDTO inventoryDTO,
            List<ProductImageDTO> images,
            CategoryDTO categoryDTO) {

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscount(),
                product.getSku(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                categoryDTO,
                inventoryDTO,
                images
        );
    }

    default ProductAdminResponse toProductAdminResponse(
            Product product,
            ProductImageDTO imageDTO,
            CategoryDTO categoryDTO,
            InventoryDTO inventoryDTO
    ){
        return new ProductAdminResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscount(),
                product.getSku(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                categoryDTO,
                inventoryDTO,
                imageDTO
        );
    }
    default ProductWithImageAndCategory toProductWithImagesAndCategory(
            Product product,
            ProductImageDTO productImageDTO,
            CategoryDTO categoryDTO
    ){
        return new ProductWithImageAndCategory(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscount(),
                product.getSku(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                categoryDTO,
                productImageDTO
        );
    }


}
