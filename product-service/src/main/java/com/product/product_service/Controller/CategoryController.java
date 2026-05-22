package com.product.product_service.Controller;

import com.product.product_service.DTOs.Category.CategoryDTO;
import com.product.product_service.Services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> fetchAll(){
        List<CategoryDTO> categoryDTOS = this.categoryService.fetchAll();

        return ResponseEntity.ok(categoryDTOS);
    }
}
