package com.product.product_service.DTOs.Category;

import java.util.List;

public record CategoryDTO (
        Long categoryId,
        String name,
        List<Long> productIds
){
}
