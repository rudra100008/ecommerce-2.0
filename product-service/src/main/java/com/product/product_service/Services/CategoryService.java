package com.product.product_service.Services;

import com.product.product_service.DTOs.Category.CategoryDTO;
import com.product.product_service.DTOs.Category.CategoryRequest;
import com.product.product_service.Entities.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    Category findOrCreate(CategoryRequest categoryRequest);

    CategoryDTO findById(Long id);

    List<CategoryDTO> fetchAll();

}
