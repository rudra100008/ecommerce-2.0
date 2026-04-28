package com.product.product_service.DTOs.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest (
        @NotBlank(message = "Category is required.")
        @NotNull(message = "Category is required.")
        String categoryName
){
}
