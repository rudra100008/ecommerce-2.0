package com.product.product_service.Mappers;

import com.product.product_service.DTOs.ProductImage.ProductImageDTO;
import com.product.product_service.Entities.Product;
import com.product.product_service.Entities.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    @Mapping(target = "product",ignore = true)
    @Mapping(target = "folder",ignore = true)
    @Mapping(target = "publicId",ignore = true)
    ProductImage toProductImage(ProductImageDTO dto);

    @Mapping(target = "imageUrl", source = "imageUrl")
    ProductImageDTO toProductImageDTO(ProductImage productImage);


    default List<ProductImageDTO> toProductImageDTOs(List<ProductImage> productImages){
        if(productImages == null){
            return new ArrayList<>();
        }
        return productImages.stream()
                .map(this::toProductImageDTO)
                .toList();
    }
}
