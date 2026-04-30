package com.product.product_service.ServiceImpls;

import com.product.product_service.DTOs.Category.CategoryDTO;
import com.product.product_service.DTOs.Category.CategoryRequest;
import com.product.product_service.Entities.Category;
import com.product.product_service.Exceptions.ResourceNotFoundException;
import com.product.product_service.Mappers.CategoryMapper;
import com.product.product_service.Repository.CategoryRepository;
import com.product.product_service.Services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public Category findOrCreate(CategoryRequest categoryRequest) {
        return this.categoryRepository.findByNameIgnoreCase(categoryRequest.categoryName())
                .orElseGet(()->{
                    Category category = Category.builder()
                            .name(categoryRequest.categoryName().trim())
                            .build();
                    return this.categoryRepository.save(category);
                });
    }

    @Override
    public CategoryDTO findById(Long id) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found."));
        return  this.categoryMapper.toCategoryDTO(category);
    }
}
