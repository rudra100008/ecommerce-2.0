package com.product.product_service.Mappers;

import com.product.product_service.DTOs.ProductImage.ProductImageDTO;
import com.product.product_service.Entities.Product;
import com.product.product_service.Entities.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    @Mapping(target = "products",ignore = true)
    ProductImage toProductImage(ProductImageDTO dto);

    @Mapping(source = "product",target ="productIds", qualifiedByName = "mapProductsToIds")
    ProductImageDTO toProductImageDTO(ProductImage productImage);


    @Named("mapProductsToIds")
    default List<Long> mapProductsToIds(List<Product> products){
        if (products == null){
            return List.of();
        }
        return products.stream()
                .map(Product::getId)
                .toList();
    }
}
