package com.product.product_service.Mappers;

import com.product.product_service.DTOs.Category.CategoryDTO;
import com.product.product_service.DTOs.Category.CategoryRequest;
import com.product.product_service.Entities.Category;
import com.product.product_service.Entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "categoryName", target = "name")
    Category toCategory(CategoryRequest request);


    @Mapping(source = "id",target = "categoryId")
    @Mapping(source = "products",target = "productIds", qualifiedByName = "mapProductToProductIds")
    CategoryDTO toCategoryDTO(Category category);

    @Named("mapProductToProductIds")
    default List<Long> mapProductToProductIds(List<Product> products){
        if(products == null){
            return List.of();
        }
        return products.stream()
                .map(Product::getId)
                .toList();
    }
}
